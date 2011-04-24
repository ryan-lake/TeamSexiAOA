#include <cmath>
#include <sstream>
#include <algorithm>

namespace aoa{

    //! Type produced by the log power DFT function
    typedef std::vector<float> log_pwr_dft_type;

    /*!
     * Get a logarithmic power DFT of the input samples.
     * Samples are expected to be in the range [-1.0, 1.0].
     * \param samps a pointer to an array of complex samples
     * \param nsamps the number of samples in the array
     * \return a real range of DFT bins in units of dB
     */
    template <typename T> log_pwr_dft_type log_pwr_dft(
        const std::complex<T> *samps, size_t nsamps
    );
}//end namespace

/***********************************************************************
 * Helper functions
 **********************************************************************/
namespace {/*anon*/

    static const double pi = double(std::acos(-1.0));

    //! Round a floating-point value to the nearest integer
    template <typename T> int iround(T val){
        return (val > 0)? int(val + 0.5) : int(val - 0.5);
    }


    //! Compute an FFT with pre-computed factors using Cooley-Tukey
    template <typename T> std::complex<T> ct_fft_f(
        const std::complex<T> *samps, size_t nsamps,
        const std::complex<T> *factors,
        size_t start = 0, size_t step = 1
    ){
        if (nsamps == 1) return samps[start];
        std::complex<T> E_k = ct_fft_f(samps, nsamps/2, factors+1, start,      step*2);
        std::complex<T> O_k = ct_fft_f(samps, nsamps/2, factors+1, start+step, step*2);
        return E_k + factors[0]*O_k;
    }

    //! Compute an FFT for a particular bin k using Cooley-Tukey
    template <typename T> std::complex<T> ct_fft_k(
        const std::complex<T> *samps, size_t nsamps, size_t k
    ){
        //pre-compute the factors to use in Cooley-Tukey
        std::vector<std::complex<T> > factors;
        for (size_t N = nsamps; N != 0; N /= 2){
            factors.push_back(std::exp(std::complex<T>(0, T(-2*pi*k/N))));
        }
        return ct_fft_f(samps, nsamps, &factors.front());
    }

} //namespace /*anon*/

namespace aoa{

    template <typename T> log_pwr_dft_type log_pwr_dft(
        const std::complex<T> *samps, size_t nsamps
    ){
        if (nsamps & (nsamps - 1))
            throw std::runtime_error("num samps is not a power of 2");

        //compute the window
        double win_pwr = 0;
        std::vector<std::complex<T> > win_samps;
        for(size_t n = 0; n < nsamps; n++){
            //double w_n = 1;
            //double w_n = 0.54 //hamming window
            //    -0.46*std::cos(2*pi*n/(nsamps-1))
            //;
            double w_n = 0.35875 //blackman-harris window
                -0.48829*std::cos(2*pi*n/(nsamps-1))
                +0.14128*std::cos(4*pi*n/(nsamps-1))
                -0.01168*std::cos(6*pi*n/(nsamps-1))
            ;
            //double w_n = 1 // flat top window
            //    -1.930*std::cos(2*pi*n/(nsamps-1))
            //    +1.290*std::cos(4*pi*n/(nsamps-1))
            //    -0.388*std::cos(6*pi*n/(nsamps-1))
            //    +0.032*std::cos(8*pi*n/(nsamps-1))
            //;
            win_samps.push_back(T(w_n)*samps[n]);
            win_pwr += w_n*w_n;
        }

        //compute the log-power dft
        log_pwr_dft_type log_pwr_dft;
        for(size_t k = 0; k < nsamps; k++){
            std::complex<T> dft_k = ct_fft_k(&win_samps.front(), nsamps, k);
            log_pwr_dft.push_back(float(
                + 20*std::log10(std::abs(dft_k))
                - 20*std::log10(T(nsamps))
                - 10*std::log10(win_pwr/nsamps)
                + 3
            ));
        }

        return log_pwr_dft;
    }
}
