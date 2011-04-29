package zAttempts;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class DBService extends Service {
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	/*
	private  SQLiteDatabase mDb; //Database 
	private QDFDatabaseAdapter mAdapter;
	
	public DBServive(Context contxt){
		helper = new QDFDatabaseAdaper(contxt);
	}

	public SQLiteDatabase getDb(){
		if(mDb == null){
			;
		}
		
		}
	}
*/
}
