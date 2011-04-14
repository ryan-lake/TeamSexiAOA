package act.QDF;

import java.sql.Timestamp;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;
import com.Services.PollingService;

import act.QDF.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DebugConsole extends Activity{
    /** Called when the activity is first created. */

    public static final String POLLINGACTION = "act.QDF.DebugConssole.Polling";
    public static final String UPDATEACTION = "act.QDF.DebugConsole.Update";
    public static final String TOGTOGUIACTION = "act.QDF.DebugConsole.ToggleToGUI";
    
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
    QDFDbAdapter mAdapter;
    //Cursor cursor;
    
    //Test area 
    //snap
	//ConsoleBR mConsoleBR;
	
	private BroadcastReceiver  mBR;
	IntentFilter mConsoleIf;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug);        
        
        Button updateButton = (Button)findViewById(R.id.updater);        
        updateButton.setOnClickListener(mUpdateListener);
        
        Button toggleToGUIButton = (Button)findViewById(R.id.toggleToGUIButton);        
        toggleToGUIButton.setOnClickListener(mToggleListener);/*new OnClickListener() {
            public void onClick(View v) {
            	toggleActiveities();
            	/*
            	Intent temp = new Intent(this,act.QDF.QDFGUI.class);
        		//temp.setClass(packageContext, cls);
        		temp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		//temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        		 
        		 
            	startActivity(temp);
            	
            	
            }
            });
            */
        //this.set
        //consoleBR = new ConsoleBR();
        
        mConsoleIf = new IntentFilter();
        mConsoleIf.addAction(this.POLLINGACTION);
        
//        this.yourReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//        		TextView status = (TextView) findViewById(R.id.StatusText);        		
//        		new UIUpdateTask().execute(status.getText()); 
//            }
//        };
        this.mBR = new BroadcastReceiver() {      	
       	            @Override
        	            public void onReceive(Context context, Intent intent) {
       	            	/*Only thing that has this class registered should be
       	            	the polling function thus we dont need to check the intent*/
       	            		new UIUpdateTask().execute();        	        		
        	            }
        	        };
        
         
        mAdapter = new QDFDbAdapter(this);
        //adapter.open();
        /*
        adapter.purgeAll();//testing
        
        adapter.loadTestData();
*/
        //DB Test verification
       //Cursor cursor = (SQLiteCursor) adapter.readData();
        /*
        Cursor cursor = (SQLiteCursor) adapter.readSettings();
        cursor.moveToFirst();
        cursor.moveToLast();
        cursor.close();
        */
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	/*
    	//this.registerReceiver(CBRS, consoleIf);  	
        this.registerReceiver(this.mBR, mConsoleIf);
        this.startService(new Intent(this,com.Services.PollingService.class));        

        mAdapter.open();
        mAdapter.purgeAll();//testing
        mAdapter.loadTestData();
        */
    }
    
    @Override
    public void  onResume(){
    	super.onResume();
    	
        this.registerReceiver(this.mBR, mConsoleIf);
        
        if(!PollingService.isRunning()){//Probably a better way to check but need working
        	this.startService(new Intent(this,com.Services.PollingService.class));        
        }
        
        mAdapter.open();
        mAdapter.purgeAll();//testing
        mAdapter.loadTestData();
    }
    @Override
    public void onPause(){
    	super.onPause();
    	//this.unregisterReceiver(consoleBR);
    	this.unregisterReceiver(this.mBR);
    }
    @Override
    public void onStop(){
    	super.onStop();
    	//this.unregisterReceiver(consoleBR);
    	mAdapter.close();
    	//this.stopService(new Intent(this,com.Services.PollingService.class));
    	//this.unregisterReceiver(this.mBR);
    }
    @Override
    public void onDestroy(){
    	//this.unregisterReceiver(receiver)
    	super.onDestroy();
    	if(!PollingService.isRunning()){
    		this.stopService(new Intent(this,com.Services.PollingService.class));   		
    	}   	
    	mAdapter.close();
    }

    //////Helper
    public void updateGUI(String newValue){
    	TextView status = (TextView) findViewById(R.id.StatusText);
		//Simulated update GUI Algorithm
        	status.setText("Status: "+"\n"+newValue);
	}
    
    protected void toggleActivities(){

    	Intent temp = new Intent(this,act.QDF.QDFGUI.class);
		temp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(temp);
		//this.finish();
		
    }
    
    
       //Listeners 
    private OnClickListener mUpdateListener = new OnClickListener() {
        public void onClick(View v) {
        	mAdapter.updateData();
        }
    };
    private OnClickListener mToggleListener = new OnClickListener() {
        public void onClick(View v) {
        	toggleActivities();
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
    			
    			Cursor cursor = (SQLiteCursor) mAdapter.readData();
    	        cursor.moveToLast();
    	        results = "--------New Record--------\n" +
    	        		//"ID: "+cursor.getString(0)+
    	        		"\nTime: "+ new Timestamp(Long.parseLong(cursor.getString(0))).toGMTString()+
    	        		"\nLocation: "+cursor.getString(1);
    	        cursor.close();
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
    }//Asynch Task
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

