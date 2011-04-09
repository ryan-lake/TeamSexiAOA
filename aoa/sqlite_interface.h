/*
*  Sqlite interface for the aoa code
*/
#pragma once

#include <stdio>
#include <sqlite3.h>

class Sqlite
{
    Sqlite(string path);
    
    bool needUpdate();
    void putDirection(int direction);
    int  getDwellTime();
    int  getFrequency();
    void confirmUpdated();
}
