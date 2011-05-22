
#include "workspace.h"
using namespace std;

//Mutex locks
//getters

    static int getData(){
            return data;
        }

    static int getPower(){
            return powerlevel;
        }
    static int getSmooth(){
            return smooth;
        }

     static int getCenFreq(){
             return cenFreq;
        }
  ///////////Setter
     static void setData(int newData){
             dataMutex.lock();
             data= newData;
             dataMutex.unlock();
         }

     static void setPower(int newPower){
             powerMutex.lock();
             powerlevel = newPower;
             powerMutex.unlock();
         }
    static void setSmooth(int newSmooth){
             smoothMutex.lock();
             smooth = newSmooth;
             smoothMutex.unlock();
         }
     static void setCenFreq(int newFreq){
             cenFreqMutex.lock();
             cenFreq = newFreq;
             cenFreqMutex.unlock();
         }


    };
