#ifndef WORKSPACE_H
#define WORKSPACE_H

#include <QMutex>

    class holder{

    private: static int data;
    private: static int powerlevel;
    private: static int smooth;
    private: static int cenFreq;

    private: static QMutex dataMutex;
    private: static QMutex powerMutex;
    private: static QMutex smoothMutex;
    private: static QMutex cenFreqMutex;


    static int getData();
    static int getPower();
    static int getSmooth();
    static int getCenFreq();
  ///////////Setter
    static void setData(int newData);
     static void setPower(int newPower);
     static void setSmooth(int newSmooth);
     static void setCenFreq(int newFreq);
 };
#endif // WORKSPACE_H
