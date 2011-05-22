#ifndef UIMANAGER_H
#define UIMANAGER_H
#include "qdfgui2.h"
class UIManager
{
public:
    UIManager();


private: QDFGUI2 gui;

public:
        QDFGUI2 getGUI();
};

#endif // UIMANAGER_H
