package test.Data;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import java.util.Random;

public class SQLLoad {
	
	
	/**
	 * Load Test vectors in to the data table
	 * 
	 * Timestamp will be set based one he system clock 
	 * 
	 * Data will change every time its called. 
	 * 
	 */
	public static long loadData(SQLiteDatabase sqlDb){
		if(sqlDb != null){
        ContentValues initialValues = new ContentValues();
        //initialValues.put(QDFDbAdapter.TIMESTAMP , System.currentTimeMillis());
         
        initialValues.put(QDFDbAdapter.LOCATION , generateNewData(360));
        initialValues.put(QDFDbAdapter.POWERLEVEL , generateNewData(1000));

        return sqlDb.insert(QDFDbAdapter.DATATABLENAME, null, initialValues);
		}
		return -1;
	}
	/*
	 * Load test vectors into the 
	 */

	public static long loadSettings(SQLiteDatabase sqlDb){
		if(sqlDb != null){
		ContentValues initialValues = new ContentValues();
		
        //initialValues.put(QDFDbAdapter.TIMESTAMP , System.currentTimeMillis());

        initialValues.put(QDFDbAdapter.DWELLTIME , generateNewData(120));
        initialValues.put(QDFDbAdapter.CENTERFREQ, generateNewData(56));
        initialValues.put(QDFDbAdapter.READ, 0);
        return sqlDb.insert(QDFDbAdapter.SETTINGSTABLENAME, null, initialValues);
		}
		return -1;
	}
	/**
	 * Will return randomly generated int between 1 and a cap
	 */
	private static int generateNewData(int cap){
		  Random rand = new Random();
		  return rand.nextInt(cap)+1;		  
	}
}
