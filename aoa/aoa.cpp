#include <uhd/utils/thread_priority.hpp>
#include <uhd/utils/safe_main.hpp>
#include <uhd/usrp/multi_usrp.hpp>
#include <uhd/types/device_addr.hpp>
#include <boost/program_options.hpp>
#include <boost/format.hpp>
#include <boost/thread.hpp>
#include <iostream>
#include <fstream>
#include <complex>
#include <math.h>

#define PI 3.1415926535897932384626433832795



// toggle determines which antenna pair is being used
// where false = east/west, and true = north/south


int UHD_SAFE_MAIN(int argc, char *argv[]){
    //initialize to base values
	size_t total_num_samps;
	double rate, freq, gain, bandwidth, p1, p2, direction, num, den;
	double array[4]={0,0,0,0}; //corresponding to e,w,n,s
	bool toggle, east, north, update = false;

	rate =    	4000000;
	freq = 	     1852500000; //sprint band
	bandwidth =     1250000;
	total_num_samps=  10000; //or some function of dwell time and samp rate
	uhd::device_addr_t dev_addr;	
    
    uhd::set_thread_priority_safe();


    //create a usrp device w/ 2 antennas
    uhd::usrp::multi_usrp::sptr usrp = uhd::usrp::multi_usrp::make(dev_addr);//need arguments

    //set the rx sample rate   
    usrp->set_rx_rate(rate);
   
    //set the rx center frequency    
    usrp->set_rx_freq(freq);
   
    //set the rx rf gain    
    usrp->set_rx_gain(gain);

    //set the rx bandwidth
    usrp->set_rx_bandwidth(bandwidth,0);
    usrp->set_rx_bandwidth(bandwidth,1);

    //set up gpio.............

    //allow for some setup time
    boost::this_thread::sleep(boost::posix_time::seconds(1)); 


while(1){

    size_t num_acc_samps = 0; //number of accumulated samples
	p1=0;  //rssi in db
	p2=0;  //rssi in db

    //get total_num_samps amount of rssi samples	
    while(num_acc_samps < total_num_samps){
       num_acc_samps += 1;
       p1+=usrp->read_rssi(0);  //abs?
       p2+=usrp->read_rssi(1);
    }

	//now average all the samples
	p1=p1/total_num_samps;		//average p1
	p2=p2/total_num_samps;		//average p2
	num_acc_samps = 0;		//reset samples taken

	// determine which oppsosing antenna is recieving the most power
	if(toggle){
		p1>p2 ? east=true : east=false;
		array[0]=p1;
		array[2]=p2;
	}
	else{
		p1>p2?north=true:north=false;	
		array[1]=p1;
		array[3]=p2;
	}

	//do we have data from both sets of antennas?
	if(toggle){
	
	   //then update direction by determining which 2 antennas 
	   //had the highest recieved power, determining which was 
	   //more powerful, taking the arctan, and adding an 
	   //appropriate quadrant offset
	   if(east && north){
		array[0]>array[1] ? num=array[1] : num=array[0];
		array[0]>array[1] ? den=array[0] : den=array[1];
		direction=(atan(num/den)*180/PI);
	   }
	   else if(east && !north){
		array[0]>array[3] ? num=array[0] : num=array[3];
		array[0]>array[3] ? den=array[3] : den=array[0];
		direction=(atan(num/den)*180/PI) + 270;
	   }
	   else if(!east && north){
		array[2]>array[1] ? num=array[2] : num=array[1];
		array[2]>array[1] ? den=array[1] : den=array[2];
		direction=(atan(num/den)*180/PI) + 90;
	   }
	   else{
		array[2]>array[3] ? num=array[2] : num=array[3];
		array[2]>array[3] ? den=array[3] : den=array[2];
		direction=(atan(num/den)*180/PI) + 180;
	   }
	
	//do we have user updates? (we do this in here so we have a 
	//full data set from both antenna pairs before changing parameters
	if (update){
		/*
		 //set the rx center frequency    
   		 usrp->set_rx_freq(freq);
		 total_num_samps=some function of dwell time and sampling rate
		*/
	update=false;//need to update database from this end as well
	}//end update checker

    }//end if toggle

        //call gpio method to toggle switches
    /*

	gpio

    */
	toggle=!toggle;

	
	


}
}

/*   old stuff



    //loop until total number of samples reached
    
    uhd::rx_metadata_t md;
    std::vector<std::complex<short> > buff(usrp->get_device()->get_max_recv_samps_per_packet());
    std::ofstream outfile(file.c_str(), std::ofstream::binary);

    while(num_acc_samps < total_num_samps){
        size_t num_rx_samps = usrp->get_device()->recv(
            &buff.front(), buff.size(), md,
            uhd::io_type_t::COMPLEX_INT16,
            uhd::device::RECV_MODE_ONE_PACKET
        );

        //handle the error codes
        switch(md.error_code){
        case uhd::rx_metadata_t::ERROR_CODE_NONE:
            break;

        case uhd::rx_metadata_t::ERROR_CODE_TIMEOUT:
            if (num_acc_samps == 0) continue;
            std::cout << boost::format(
                "Got timeout before all samples received, possible packet loss, exiting loop..."
            ) << std::endl;
            goto done_loop;

        default:
            std::cout << boost::format(
                "Got error code 0x%x, exiting loop..."
            ) % md.error_code << std::endl;
            goto done_loop;
        }

        //write complex short integer samples to the binary file
        outfile.write((const char*)&buff[0], num_rx_samps * sizeof(std::complex<short>));

        num_acc_samps += num_rx_samps;
    } done_loop:

    outfile.close();

    //finished
    std::cout << std::endl << "Done!" << std::endl << std::endl;

    return 0;
}s)
*/
