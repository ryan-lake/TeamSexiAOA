/*
*  Sqlite interface for the aoa code
*/
#pragma once

#include <iostream>
#include <string>
#include <sqlite3.h>
#include <stdlib.h>
#include <time.h>
#include <boost/lexical_cast.hpp>

using namespace std;
class Sqlite
{
public:
    Sqlite(string path);
    
    bool needUpdate();
    void putDirection(int direction, int power);
    int  getDwellTime();
    int  getFrequency();
    void confirmUpdated();
private:
    //fields
    sqlite3 *db;
    char *zErrMsg;
    int rc;
    string dbPath;

    
};

//methods
int needUpdateCallback(void*, int, char**,char**);
int dwellTimeCallback(void*, int, char**, char**);
int frequencyCallback(void*, int, char**, char**);
