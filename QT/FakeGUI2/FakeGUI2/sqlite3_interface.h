#ifndef SQLITE_H
#define SQLITE_H

#include <stdlib.h>
#include <iostream>
#include "boost/lexical_cast.hpp"
#include <sqlite3.h>

using namespace std;
class Sqlite
{
public:
    Sqlite(string);
    int* getResult();
    void setConfig(int freq, int dwellTime);
    bool updated();
private:
    string dbPath;
    sqlite3 *db;
    char *zErrMsg;
};

#endif // SQLITE_H
