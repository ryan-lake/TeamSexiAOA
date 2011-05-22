#include "sqlite3_interface.h"

//C prototypes
int getResultCallback(void *updated, int argc, char **argv, char **colNames);
int updatedCallback(void *updated, int argc, char **argv, char **colNames);

Sqlite::Sqlite(string path)
{
    dbPath = path;
}

int* Sqlite::getResult()
{
    int* array = new int[2];
    string query = "SELECT location, powerlevel FROM data ORDER BY tstamp DESC;";
    int rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    array[0] = -1;
    array[1] = -1;
    rc = sqlite3_exec(db, query.c_str(), getResultCallback, array, &zErrMsg);
    if ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        sqlite3_free(zErrMsg);
        /*sqlite3_close(db);
        exit(1);*/

        rc = sqlite3_exec(db, query.c_str(), getResultCallback, array, &zErrMsg);
    }
    query = "DELETE FROM data;";
    rc = sqlite3_exec(db, query.c_str(), NULL, NULL, &zErrMsg);
    sqlite3_close(db);
    return array;
}

void Sqlite::setConfig(int freq, int dwellTime)
{
    string query = "INSERT INTO settings ( centerfreq, dwelltime, read ) VALUES (" +
            boost::lexical_cast<string>(freq) +"," +
            boost::lexical_cast<string>(dwellTime) + ", 0 );";

    int rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }
    rc = sqlite3_exec(db, query.c_str(), NULL, NULL, &zErrMsg);
    if ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        sqlite3_free(zErrMsg);
        /*sqlite3_close(db);
        exit(1);*/

        rc = sqlite3_exec(db, query.c_str(), NULL, NULL, &zErrMsg);
    }
    sqlite3_close(db);
}

bool Sqlite::updated()
{
    string query = "SELECT * FROM settings;";
    bool updated = true;
    int rc = sqlite3_open(dbPath.c_str(), &db);
    if ( rc )
    {
        cout << "Cannot open db: " << sqlite3_errmsg(db);
        sqlite3_close(db);
        exit(1);
    }

    rc = sqlite3_exec(db, query.c_str(), updatedCallback, &updated, &zErrMsg);
    if ( rc != SQLITE_OK )
    {
        cout << "Cannot execute query: " << query << "\n" << zErrMsg;
        sqlite3_free(zErrMsg);
        /*sqlite3_close(db);
        exit(1);
        */
        rc = sqlite3_exec(db, query.c_str(), updatedCallback, &updated, &zErrMsg);
    }
    sqlite3_close(db);
    return updated;
}

int updatedCallback(void *updated, int argc, char **argv, char **colNames)
{
    *static_cast<bool*>(updated) = false;
    return 0;
}

int getResultCallback(void *dwellTime, int argc, char **argv, char **colNames)
{
    static_cast<int*>(dwellTime)[0] = atoi(argv[0]);
    static_cast<int*>(dwellTime)[1] = atoi(argv[1]);
    return 0;
}

