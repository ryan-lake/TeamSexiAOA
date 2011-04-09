package com.SQLiteDatabaseWrapper;

import test.Data.SQLLoad;
import act.DebugConsole.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;
/**
 * Class acts as an Adapter to access the DB. It should hold a 
 * The primary reason that this is an independent service is to make it independent
 *  of any particular Activity. 
 *  
 * FIXME Implement a Binder for the service 
 * If this proves to be an issue, we can move to have the service be bound to what 
 * ever active GUI is present. 
 * 		Service Manager might be required at that point
 * 
 * FIXME Need a broadcast reciever to handle the outgoing transmission to the DB
 * 
 */
public class QDFDbAdapter{
    //Might be able to drop these into the R string xml since this DB will be static 
	
	/* Android// is most likely the root directory*/ 
	//Broadcast Receiver
	/*
	 Should cover all data transfers to and from DB:
	 -Sending Variables from GUI to the Settings Table in the DB to the C/C++ scripts
	 
	 -Once Polling service has broadcast that there is new data, 
	 	Read the new data in and pass it to the GUI to update the location
	 
	 */
	private BroadcastReceiver  BR;//
    public static final String UPDATEGUIACTION = "com.Services.UpdateGUIValues";
    public static final String GETLOCATIONACTION = "com.Services.GetLocationValues";	
	
		
	//Global DB Variables
	public static final String DBPATH = "Android/data/Ubuntu/QuallcommDirectionFinding/SQLite/";
    public static final String DBNAME = "QDFDataBase";
    public static final int DBVERSION = 1;
    
    //Global Keys
    public static final String ID = "_id";
    public static final String TIMESTAMP = "tstamp";
    
    //Table 1:
    /*
     * the Settings table will need Dwell time as a double and 
     * Center frequency
     */
    public static final String SETTINGSTABLENAME= "settings";

    //Table 1 Keys
    public static final String DWELLTIME= "dwelltime";//assumed Seconds*Proabbly will need to change*
    public static final String CENTERFREQ= "centerfreq";
    
    
    //Table 2:
    /*Data should only have one meaning-full column which will be
    the integer value between 0 and 360(degrees)
    */
    public static final String DATATABLENAME = "data";

    //Table 2 Keys
    public static final String LOCATION= "location";//location given in Degrees(0 = straight ahead = North)
   
    
    /**
     * Commands
     * 
     * We only need a limited number of SQLcommands, most of which don't
     *  need to be generated dynamically.
     *  
     *  Dynamic
     *   	Write new values from GUI to settings table in the DB
     *   		*used with static labels*
     *  Static
     *  	Create tables(Settings+data)
     *  
     *  	Query for direction data, key of timestamp
     *  	 
     *  TODO Make a full list of the static commands
     * 
     */
    //Commad Array?
    public static final String COMMAND_CREATE_TABLE_SETTINGS= "CREATE TABLE "+SETTINGSTABLENAME+" ( "+
    		ID +" INTEGER NOT NULL, "+
    		TIMESTAMP + " TIMESTAMP PRIMARY KEY, "+
    		DWELLTIME + " INTEGER NOT NULL, "+
    		CENTERFREQ + " INTEGER NOT NULL);";
    
    public static final String COMMAND_CREATE_TABLE_DATA= "CREATE TABLE "+DATATABLENAME+ " ( "+
			ID + " INTEGER NOT NULL, "+
			//TIMESTAMP + " TIMESTAMP PRIMARY KEY, "+
			TIMESTAMP + " TEXT NOT NULL, "+
   			LOCATION + " INTEGER NOT NULL);";
    ////////////////////////Local variables	
	
    private SQLiteDatabase mDb;//we only want this class manipulating the database
    private final Context mContext;
    private QDFDbHelper mDbHelper;
    
    /*
     * Serives
     * @see android.app.Service#onCreate()
     */

    
///////////////////Class
    public QDFDbAdapter(Context context){
    	mContext = context;
    }
//Functions- for database access and creation
//Void since we dont want to return  
    //public QDFDbService open() throws SQLException {

    
    public long open() throws SQLException {        
    	mDbHelper = new QDFDbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        
        //Preload Test vectors
        //SQLLoad.load(mDb);
        return (SQLLoad.load(mDb));
    }
    
    
    public void close() {
        mDbHelper.close();
    }
    
    
    
    /////Test functions
    public Cursor fetchAllData() {//Test

        return mDb.query(this.DATATABLENAME, new String[] {this.ID,this.TIMESTAMP,this.LOCATION},
        		null, null, null, null, null);
    }
    
    public boolean purge(){//clear the Database
    	long temp = -1;
    	try{
    	if(mDb !=null){
    		return (mDb.delete(DATATABLENAME, null, null)>0) && (mDb.delete(SETTINGSTABLENAME, null, null)>0);
    	}
    	}catch(Exception e){
    		//Database is empty?
    	}
    	return  false;
    	
    }
    
    //////TAble specific
    
    public long updateSettings(int dwellTime, int centerFreque) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(this.DWELLTIME, dwellTime);
        initialValues.put(this.CENTERFREQ, centerFreque);

        return mDb.insert(this.SETTINGSTABLENAME, null, initialValues);
    }
    
        
///////////////////Helper Class to perform all the SQLite DB accesses and commands
	private static class QDFDbHelper extends SQLiteOpenHelper {

		public QDFDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			// TODO may need Cursor factory
			
			super(context, DBNAME, null, DBVERSION);
			
		}
		
		public QDFDbHelper(Context context){
			super(context, DBNAME, null, DBVERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase sqlDB) {//called by getWriteableDatabase()
            try{
            	sqlDB.execSQL(COMMAND_CREATE_TABLE_SETTINGS);
            }catch(Exception e){
            	e.printStackTrace();
            }
            
            try{
            	sqlDB.execSQL(COMMAND_CREATE_TABLE_DATA);
        	}catch(Exception e){
        		e.printStackTrace();
            }
		}
		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
		}
		
	
	}//Helper
}//Service
