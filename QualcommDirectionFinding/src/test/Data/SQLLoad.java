package test.Data;

import java.sql.Timestamp;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class SQLLoad {
	public static long load(SQLiteDatabase sqlDb){
		
        ContentValues initialValues = new ContentValues();
        
        //Timestamp temp = new Timestamp(System.currentTimeMillis());
        //Long.toString((System.currentTimeMillis()))
        initialValues.put(QDFDbAdapter.TIMESTAMP , new Timestamp(System.currentTimeMillis()).toString());
        //initialValues.put(QDFDbAdapter.TIMESTAMP,"bam");
        initialValues.put(QDFDbAdapter.LOCATION , 10);
        initialValues.put(QDFDbAdapter.ID, 1);

        return sqlDb.insert(QDFDbAdapter.DATATABLENAME, null, initialValues);
	}
}
