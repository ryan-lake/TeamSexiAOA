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
    string query = "SELECT read FROM settings WHERE read=0";
    bool needUpdate = false;
    rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), needUpdateCallback, &needUpdate, &zErrMsg);
    if ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
    }
    sqlite3_close(db);
    return needUpdate;
}

void Sqlite::putDirection(int direction)
{
    string query = "INSERT INTO data (location) VALUES (" + boost::lexical_cast<string>( direction ) + ");";
    rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), 0, 0, &zErrMsg);
    if ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        sqlite3_free(zErrMsg);
        sqlite3_close(db);
        exit(1);
    }
    sqlite3_close(db);
}

int needUpdateCallback(void *needUpdate, int argc, char **argv, char **colNames)
{
    *static_cast<bool*>(needUpdate) = true;
    return 0;
}




