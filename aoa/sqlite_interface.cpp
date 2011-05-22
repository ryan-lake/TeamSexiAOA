/* 
 * Sqlite interface for AOA
 */

#include "sqlite_interface.h"

Sqlite::Sqlite(string path)
{
    dbPath = path;
}

bool Sqlite::needUpdate()
{
    string query = "SELECT read FROM settings WHERE read=0;";
    bool needUpdate = false;
    rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), needUpdateCallback, &needUpdate, &zErrMsg);
    while ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        /*
	sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
	*/
	rc = sqlite3_exec(db, query.c_str(), needUpdateCallback, &needUpdate, &zErrMsg);
    }
    sqlite3_close(db);
    return needUpdate;
}

void Sqlite::putDirection(int direction, int power)
{
    string query = "INSERT INTO data ( location, powerlevel ) VALUES (" + boost::lexical_cast<string>( direction ) + ", " + boost::lexical_cast<string>( power ) + ");";
    rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), 0, 0, &zErrMsg);
    while ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        /*
	sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
	*/
	rc = sqlite3_exec(db, query.c_str(), 0, 0, &zErrMsg);
    }
    sqlite3_close(db);
}
int Sqlite::getDwellTime()
{
    string query = "SELECT dwelltime FROM settings WHERE read=0 ORDER BY tstamp DESC;";
    rc = sqlite3_open(dbPath.c_str(), &db);
    int dwellTime = 500;
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), dwellTimeCallback, &dwellTime, &zErrMsg);
    while ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
	/*
        sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
	*/
	rc = sqlite3_exec(db, query.c_str(), dwellTimeCallback, &dwellTime, &zErrMsg);
    }
    sqlite3_close(db);
    return dwellTime;
}

int Sqlite::getFrequency()
{
    string query = "SELECT centerfreq FROM settings WHERE read=0 ORDER BY tstamp DESC;";
    rc = sqlite3_open(dbPath.c_str(), &db);
    int frequency = 1852500000;
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), frequencyCallback, &frequency, &zErrMsg);
    while ( rc != SQLITE_OK )
    {
    	
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        /*
	sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
	*/
	rc = sqlite3_exec(db, query.c_str(), frequencyCallback, &frequency, &zErrMsg);
    }
    sqlite3_close(db);
    return frequency;
}

void Sqlite::confirmUpdated()
{
    string query = "UPDATE settings SET read=1 WHERE read=0;";
    rc = sqlite3_open(dbPath.c_str(), &db);
    int frequency = 1852500000;
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), 0, 0, &zErrMsg);
    while ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        /*sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
	*/
	rc = sqlite3_exec(db, query.c_str(), 0, 0, &zErrMsg);
    }
    sqlite3_close(db);
}


int needUpdateCallback(void *needUpdate, int argc, char **argv, char **colNames)
{
    *static_cast<bool*>(needUpdate) = true;
    return 0;
}

int dwellTimeCallback(void *dwellTime, int argc, char **argv, char **colNames)
{
    *static_cast<int*>(dwellTime) = atoi(argv[0]);
    return 0;
}

int frequencyCallback(void *frequency, int argc, char **argv, char **colNames)
{
    *static_cast<int*>(frequency) = atoi(argv[0]);
    return 0;
}



