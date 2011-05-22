
///////////////////////////////////////////////////////
/*************************************************************
/////////////////////////////////////////////////////////////////////
         ___           ___           ___           ___
        /\  \         /\  \         /\  \         /\__\
        \:\  \       /::\  \       /::\  \       /::|  |
         \:\  \     /:/\:\  \     /:/\:\  \     /:|:|  |
         /::\  \   /::\~\:\  \   /::\~\:\  \   /:/|:|__|__
        /:/\:\__\ /:/\:\ \:\__\ /:/\:\ \:\__\ /:/ |::::\__\
       /:/  \/__/ \:\~\:\ \/__/ \/__\:\/:/  / \/__/~~/:/  /
      /:/  /       \:\ \:\__\        \::/  /        /:/  /
      \/__/         \:\ \/__/        /:/  /        /:/  /
                     \:\__\         /:/  /        /:/  /
                      \/__/         \/__/         \/__/
         ___           ___           ___
        /\  \         /\  \         |\__\          ___
       /::\  \       /::\  \        |:|  |        /\  \
      /:/\ \  \     /:/\:\  \       |:|  |        \:\  \
     _\:\~\ \  \   /::\~\:\  \      |:|__|__      /::\__\
    /\ \:\ \ \__\ /:/\:\ \:\__\ ____/::::\__\  __/:/\/__/
    \:\ \:\ \/__/ \:\~\:\ \/__/ \::::/~~/~    /\/:/  /
     \:\ \:\__\    \:\ \:\__\    ~~|:|~~|     \::/__/
      \:\/:/  /     \:\ \/__/      |:|  |      \:\__\
       \::/  /       \:\__\        |:|  |       \/__/
        \/__/         \/__/         \|__|

/////////////////////////////////////////////////////////////////////
 *///********************************************************
///////////////////////////////////////////////////

#include <uhd/utils/thread_priority.hpp>
#include <uhd/utils/safe_main.hpp>
#include <uhd/usrp/multi_usrp.hpp>
#include <uhd/usrp/dboard_iface.hpp>
#include <uhd/types/device_addr.hpp>

#include <boost/program_options.hpp>
#include <boost/format.hpp>
#include <boost/thread.hpp>

#include <iostream>
#include <iomanip>
#include <fstream>
#include <complex>
#include <math.h>
#include <time.h>

#include "sqlite_interface.h"
#include "aoa.h"

#define EAST  0
#define NORTH 1
#define WEST  2
#define SOUTH 4

//****************************************************************

int UHD_SAFE_MAIN(int argc, char *argv[]) {


    // initialize to base values
    // toggle determines which antenna pair is being used,
    //    where false = east/west, and true = north/south

    bool toggle, east, north, update = false;
    int maxSamps;
    float p1, p2, direction; //power pairs and direction
    float old_east,old_west,old_north,old_south = 0;
    float power[4] = {0, 0, 0, 0}; //corresponding to e,n,w,s
    double rate, freq, gain, bandwidth, num, den, alpha, beta;
    size_t num_acc_samps;
    size_t num_samps, num_bins;
    size_t num_rx_samps = 0;
    time_t start_time, stop_time;


    rate = 4000000;
    freq = 1852500000; //sprint band
    bandwidth = 1250000;
    num_samps = 1000; // num samps
    gain = 30; //needs empirical testing
    alpha = 0.9;
    uhd::device_addr_t dev_addr;
    uhd::rx_metadata_t md;
    uhd::set_thread_priority_safe();

    //create data buffers and initialize them
    std::complex<float> buff0[num_samps];
    std::complex<float> buff1[num_samps];
    double filt0[num_samps], filt1[num_samps];
    double magsq0[num_samps], magsq1[num_samps];

    std::vector<std::complex<float>* > buffers(2);
    buffers[0] = &buff0[0];
    buffers[1] = &buff1[0];

    for (int i = 0; i < num_samps; i++) {
        buff0[i] = 0;
        buff1[i] = 0;
        magsq0[i] = 0;
        magsq0[i] = 0;
    }

    //create a usrp device w/ 2 antennas
    //std::cout << std::endl;
    uhd::usrp::multi_usrp::sptr usrp = uhd::usrp::multi_usrp::make(dev_addr);
    usrp->set_rx_subdev_spec(uhd::usrp::subdev_spec_t("A: B:"), 0);

    //print out probe results
    //std::cout << boost::format("Using Device: %s") % usrp->get_pp_string() << std::endl;

    //set the rx sample rate
    usrp->set_rx_rate(rate);

    //set the rx center frequency
    usrp->set_rx_freq(freq);

    //set the rx rf gain
    usrp->set_rx_gain(gain);

    //set the rx bandwidth
    usrp->set_rx_bandwidth(bandwidth, 0);
    usrp->set_rx_bandwidth(bandwidth, 1);

    //allow for some setup time
    boost::this_thread::sleep(boost::posix_time::seconds(1));

    //setup streaming
    maxSamps = usrp->get_device()->get_max_recv_samps_per_packet();
    //uhd::stream_cmd_t stream_cmd(uhd::stream_cmd_t::STREAM_MODE_NUM_SAMPS_AND_DONE);
    //stream_cmd.num_samps = total_num_samps;
    //stream_cmd.stream_now = false;
    //usrp->issue_stream_cmd(stream_cmd);

    //std::cout << usrp->get_pp_string();

    //setup daughterboard gpio........

    //create daughterboard interfaces
    boost::shared_ptr<uhd::usrp::dboard_iface> db_a = usrp->get_rx_dboard_iface(0);
    boost::shared_ptr<uhd::usrp::dboard_iface> db_b = usrp->get_rx_dboard_iface(1);

    //initialize data direction (pin 15s out)
    db_a->set_gpio_ddr(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0xf000);
    db_b->set_gpio_ddr(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0xf000);

    //initialize gpio pins to 0b 1000 0000 0000 0000 (pin 15s high)
    db_a->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x0000, 0xf000);
    db_b->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0xf000);

    //wait long enough to measure
    boost::this_thread::sleep(boost::posix_time::seconds(1));

    /*// gpio verification tests**********************************

    std::cout << boost::format("gpio0: %f") %  db_a->get_gpio_out  (uhd::usrp::dboard_iface::UNIT_RX)<< std::endl;
    std::cout << boost::format("gpio0: %f") %  db_b->get_gpio_out(uhd::usrp::dboard_iface::UNIT_RX)<< std::endl;

    //set gpio pins to 0b 0000 0000 0000 0000 (pin 15 low)
    db_a->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0x8000);
    db_b->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x0000, 0x8000);

    //wait long enough to verify
    boost::this_thread::sleep(boost::posix_time::seconds(5));

  ///display for verification
    std::cout << boost::format("gpio0: %f") %  db_a->get_gpio_out  (uhd::usrp::dboard_iface::UNIT_RX)<< std::endl;
    std::cout << boost::format("gpio0: %f") %  db_b->get_gpio_out(uhd::usrp::dboard_iface::UNIT_RX)<< std::endl;

    //set gpio pins to 0b 1000 0000 0000 0000 (pin 15 high)
    db_a->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x0000, 0x8000);
    db_b->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0x8000);

    boost::this_thread::sleep(boost::posix_time::seconds(5));

    //*///end gpio verification tests*******************************


    //start continuous streaming
    usrp->issue_stream_cmd(uhd::stream_cmd_t::STREAM_MODE_START_CONTINUOUS);

    //------------------------------------------------------------------
    //-- Main loop
    //------------------------------------------------------------------

    while (true) {
	
	uhd::stream_cmd_t stream_cmd		 (uhd::stream_cmd_t::STREAM_MODE_NUM_SAMPS_AND_DONE);
    stream_cmd.num_samps = num_samps;
    stream_cmd.stream_now = true;
    usrp->issue_stream_cmd(stream_cmd);
//usrp->issue_stream_cmd(uhd::stream_cmd_t::STREAM_MODE_NUM_SAMPS_AND_DONE);
        //std::cout << "inside main" << std::endl;
        beta = 1.00 - alpha;

        //read a buffer's worth of samples every iteration
        num_rx_samps = usrp->get_device()->recv(buffers, num_samps, md, uhd::io_type_t::COMPLEX_FLOAT32, uhd::device::RECV_MODE_FULL_BUFF);
        if (num_rx_samps != num_samps) continue;
        //std::cout << "got all samples" << std::endl;
         //uhd::stream_cmd_t stream_cmd(uhd::stream_cmd_t::STREAM_MODE_STOP_CONTINUOUS);
         stream_cmd.stream_now = true;
    usrp->issue_stream_cmd(stream_cmd);
        //get mag squared for both buffers
        for (int i = 0; i < num_samps; i++) {
            magsq0[i] = std::norm(buff0[i]);
            magsq1[i] = std::norm(buff1[i]);
        }//end mag squared for
         // std::cout << "finished mag squared" << std::endl;

	
         //iir 
         if(toggle){// east/west pair

            //single pole lowpass iir filter
            for (int j = 0; j < num_samps; j++) {
                
                p1 = (alpha * old_east) + (beta * magsq0[j]);
                p2 = (alpha * old_west) + (beta * magsq1[j]);
                old_north = p1;
                old_south = p2;                 
            }// end filter for

            (p1 > p2) ? east = true : east = false;
            power[0] = p1;
            power[2] = p2;
            db_a->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x0000, 0xf000);
            db_b->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0xf000);
             boost::this_thread::sleep(boost::posix_time::seconds(1));
            
         }else{// north/south pair
            for (int j = 0; j < num_samps; j++) {
             
                p1 = (alpha * old_north) + (beta * magsq0[j]);
                p2 = (alpha * old_south) + (beta * magsq1[j]); 
                old_north = p1;
                old_south = p2;      
            }// end filter for

            (p1 > p2) ? north = true : north = false;
            power[1] = p1;
            power[3] = p2; 
            db_a->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x8000, 0xf000);
            db_b->set_gpio_out(uhd::usrp::dboard_iface::UNIT_RX, 0x0000, 0xf000);           
            boost::this_thread::sleep(boost::posix_time::seconds(1));
         }//end 
            //std::cout << "finished iir" << std::endl;
       	
        /*//verify iq pairs
        for(int i = 0; i < num_samps; i++)
          {
          std::cout <<"complex pair at buff0-"<<i<<" = "<< buff0[0]<< std::endl;
          std::cout <<"complex pair at buff1-"<<i<<" = "<< buff1[0]<< std::endl;
          }
       //*/

        //do we have data from both sets of antennas?
        if (toggle) {

            //then update direction by determining which 2 antennas
            //had the highest recieved power, determining which was
            //more powerful, taking the arctan, and adding an
            //appropriate quadrant offset

            if (east && north) { //quadrant I
                power[0] > power[1] ? num = power[1] : num = power[0];
                power[0] > power[1] ? den = power[0] : den = power[1];
                direction = (atan(den/num)*(180 / pi));
            }else if (!east && north) { //quadrant II
                power[2] > power[1] ? num = power[2] : num = power[1];
                power[2] > power[1] ? den = power[1] : den = power[2];
                direction = (atan( den/num)*(180 / pi)) + 90;
            }//remove? Q3-4 to trouble shoot??, checking offset??
            else if (!east && !north) { //quadrant III
                power[2] > power[3] ? num = power[2] : num = power[3];
                power[2] > power[3] ? den = power[3] : den = power[2];
                direction = (atan(den/num)*(180 /pi))+180;
            } else if (east && !north) { //quadrant IV
                power[0] > power[3] ? num = power[0] : num = power[3];
                power[0] > power[3] ? den = power[3] : den = power[0];
                direction = (atan(den/num)*(180 / pi)) + 270;
            } else {
}
		//system("clear");
                std::cout << "Direction:     " << direction << std::endl;
              //  std::cout << "0 " << power[0] << std::endl;
              //  std::cout << "1 " << power[1] << std::endl;
              //  std::cout << "2 " << power[2] << std::endl;
              //  std::cout << "3 " << power[3] << std::endl;
        //}// end if/else direction setting
	std::cout << "0 " << power[0] << std::endl;
        std::cout << "1 " << power[1] << std::endl;
        std::cout << "2 " << power[2] << std::endl;
        std::cout << "3 " << power[3] << std::endl;
        
        //Sqlite::putDirection(direction);

        // time(&stop_time);
        // diff = difftime(stop_time,start_time);
        // std::cout << std::fixed << std::setprecision(4)<< "processing time: " << diff <<std::endl;

        //do we have user updates? (we do this in here so we have a
        //full data set from both antenna pairs before changing parameters
        if (update) {            /*
        //set the rx center frequency
            usrp->set_rx_freq(freq);
                  total_num_samps=some function of dwell time and sampling rate
             */
        }//end if update

    }//end toggle check if

    toggle = !toggle; //update which antennas are being used

}//end main loop.....!......!!.....!.!!.!!!

return 0;

}//end aoa.cpp
