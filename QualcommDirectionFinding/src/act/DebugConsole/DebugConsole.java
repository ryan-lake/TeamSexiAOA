package act.DebugConsole;

import java.sql.Timestamp;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

import test.Data.USRPVectorsFrame;

import act.DebugConsole.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DebugConsole extends Activity{
    /** Called when the activity is first created. */

    public static final String POLLINGACTION = "act.DebugConsole.Polling";
    public static final String UPDATEACTION = "act.DebugConsole.Update";
    /*
    //SQLight data base setup fields->Handler
    The database might need to be held in a Service wrapper...
    
     Basically so that it is not tied to the activity but it lives in its own world,
     no additional thread
     
     It would need to be a bound service, the bound service would provide a full interface
     to the DB but would be independent from the Activity otherwise we are recreating every time,
	   *one static DB service might keep the memory overhead low then having 2 instances in both activities

To prevent multiple instances of services to access the db maybe a static services manager class should be used
	can be called to inialize all the services from either Activity


	toggleing between activities should be able to move the basic state of the current active GUI easily,
		*class for BR?
		*class for service/Bound services
		

   */ 
    //URI
    //cursor
    //public static SQLiteDatabase sqlDB;
    
    //Database
    QDFDbAdapter adapter;
    Cursor cursor;
    //TODO set up cursor factory
    
    //Test area 
    //snap
	ConsoleBR consoleBR;
	private BroadcastReceiver  yourReceiver;
	IntentFilter consoleIf;
	public static Intent hmm;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        
        Button updateButton = (Button)findViewById(R.id.updater);        
        updateButton.setOnClickListener(mUpdateListener);
        //this.set
        //consoleBR = new ConsoleBR();
        
        consoleIf = new IntentFilter();
        consoleIf.addAction(POLLINGACTION);
        
//        this.yourReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//        		TextView status = (TextView) findViewById(R.id.StatusText);        		
//        		new UIUpdateTask().execute(status.getText()); 
//            }
//        };
        this.yourReceiver = new BroadcastReceiver() {
        	
       	            @Override
        	            public void onReceive(Context context, Intent intent) {
        	        		//TextView status = (TextView) findViewById(R.id.StatusText);        		
        	        		new UIUpdateTask().execute(); 
        	            }
        	        };
        
        //consoleBR = new ConsoleBR();         
        
        //this.startService(new Intent(this,com.Services.PollingService.class));        
        
        adapter = new QDFDbAdapter(this);
        long temp = adapter.open();
        adapter.purge();//testing
        temp = adapter.open();

        
        cursor = (SQLiteCursor) adapter.fetchAllData();
        cursor.moveToFirst();
        cursor.moveToLast();
 
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	//this.registerReceiver(CBRS, consoleIf);  	
        this.registerReceiver(this.yourReceiver, consoleIf);
        //this.registerReceiver(consoleBR, consoleIf);

    }
    @Override
    public void onPause(){
    	super.onPause();
    	//this.unregisterReceiver(consoleBR);
    	this.unregisterReceiver(this.yourReceiver);
    }
    @Override
    public void onStop(){
    	super.onStop();
    	//this.unregisterReceiver(consoleBR);
    	this.unregisterReceiver(this.yourReceiver);
    }
    public void onDestroy(){
    	//this.unregisterReceiver(receiver)
    	super.onDestroy();
    }
    
    public void updateGUI(String newValue){
    	TextView status = (TextView) findViewById(R.id.StatusText);
		//Simulated update GUI Algorithm
        	status.setText(newValue);
	}
        
    private OnClickListener mUpdateListener = new OnClickListener() {
        public void onClick(View v) {
        	USRPVectorsFrame.buffReady = true;
        }
    };
//-----------------------------
    /**
     * Class that provides AsyncTask to derive the correct course of action to update the GUI
     *
     */
    public class UIUpdateTask extends AsyncTask<CharSequence,Void, String>{
    	//<parameters,progress, result>
    		public UIUpdateTask() {
    			super();

    		}
    		
    		@Override
       		protected String doInBackground(CharSequence... arg0) {
    	    	//TextView status = (TextView) findViewById(R.id.StatusText);
    			//Simulated Algorithm
    	    	//String text = (String)status.getText();
    	        String results;
    			
    			cursor = (SQLiteCursor) adapter.fetchAllData();
    	        cursor.moveToFirst();
    	        results = cursor.getString(0)+"\n"+  (new Timestamp(Long.parseLong(cursor.getString(1)))).toString()+"\n"+cursor.getString(2);
    	        
    	            	        
    			return results;
    			
    		}
    		/*
    		@Override
    		protected String doInBackground(CharSequence... arg0) {
    	    	//TextView status = (TextView) findViewById(R.id.StatusText);
    			//Simulated Algorithm
    	    	//String text = (String)status.getText();
    			CharSequence text = arg0[0];
    	    	if(text.equals("Status...")){
    	        	return ("North");
    	    	}
    	    	else if (text.equals("North")){	
    	        	return ("South");
    	    	}
    	    	else if (text.equals("South")){	
    	    		return ("East");	
    	    	}
    	    	else if (text.equals("East")){	
    	    		return ("West");
    	    	}
    	    	else if (text.equals("West")){	
    	    		return ("North");
    	    	}
    			return "FAIL";
    			
    		}
    		*/
    		@Override
    		public void onPostExecute(String result){
    			/*
    			//Works
    			TextView status = (TextView) findViewById(R.id.StatusText);
    	    	status.setText(result);
    			*/
    			updateGUI(result);
    			
    		}
    }
    //****Probably not need a full class implementation
    public  class ConsoleBR extends BroadcastReceiver {
//	Not static <receiver android:enabled="true" android:exported="false" android:name="DebugConsole$ConsoleBR"/>
    	@Override
    	public void onReceive(Context arg0, Intent arg1) {
    		/**
    		 * TODO Spawn a new Thread to handle the Algorithm update
    		 * 
    		 * This is executing in the main thread we may not need to Bind the BroadcastReceiver
    		 *
    		 */		
	
    		TextView status = (TextView) findViewById(R.id.StatusText);
    		
    		new UIUpdateTask().execute(status.getText());
    	}

    }//
}//Debugg Activity

