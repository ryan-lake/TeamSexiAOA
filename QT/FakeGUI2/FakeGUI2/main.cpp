#include <QtGui/QApplication>
#include "uimanager.h"
#include <time.h>

#include <iostream>
using namespace std;

//work space
int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    QDFGUI2 w;
    w.show();




/*
    //Exec full takes over thread
    clock_t wait;
    wait = clock();

    while(true){
        while(wait <= 1000){
        }
        wait =0;
        std::cout<<"fuk u";
    }

*/

/*
    int cenFreq;
    int dwell;
    try{
        cenFreq = w.getCenterFreq();
        dwell = w.getDwell();

        int g = 6;

        //works
        char f[] = "GG";
        w.setLocation(f);


    }catch(...){

    }
*/

    return a.exec();
}
