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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class QDFGUI extends Activity {

    public static final String POLLINGACTION = "act.QDF.QDFGUI.Polling";
    public static final String UPDATEACTION = "act.QDF.QDFGUI.Update";
    public static final String TOGTOCONACTION = "act.QDF.QDFGUI.ToggleToConsole";
    

	
    private BroadcastReceiver mBR;
	IntentFilter mConsoleIf;
	
    QDFDbAdapter mAdapter;
    
    String FreqScale;
    String TimeScale;
    
    
/*	
 * 
   public QDFGUI(){  
    super();    
    }
    */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qdfgui); 
        
        mBR = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
        		//TextView status = (TextView) findViewById(R.id.StatusText);        		
        	//	
           	new UIUpdateTask().execute(); 
        		
            }
        };
        
        mConsoleIf = new IntentFilter();
        mConsoleIf.addAction(this.POLLINGACTION);
        
        Button toggleToConButton = (Button)findViewById(R.id.toggleToConButton);        
        toggleToConButton.setOnClickListener(mToggleListener);
        
        Button updateSettingsButton = (Button)findViewById(R.id.updateSettingsButton);        
        toggleToConButton.setOnClickListener(mUpdateSettingsListener);
        
        
        mAdapter = new QDFDbAdapter(this);
        
        /*Spinner - Time*/
        Spinner spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> adapterTime = ArrayAdapter.createFromResource(
                this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapterTime);
        
        spinnerTime.setOnItemSelectedListener(new TimeOnItemSelectedListener());
        
        /*Spinner - Freq*/
        Spinner spinnerFreq = (Spinner) findViewById(R.id.spinnerFreq);
        ArrayAdapter<CharSequence> adapterFreq = ArrayAdapter.createFromResource(
                this, R.array.freq_array, android.R.layout.simple_spinner_item);
        adapterFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFreq.setAdapter(adapterFreq);
        
        spinnerFreq.setOnItemSelectedListener(new FreqOnItemSelectedListener());
        
        
    }//OnCreate
	  
    @Override
    public void onStart(){
    	super.onStart();	
/*        this.registerReceiver(mBR, mConsoleIf);
        
    	this.startService(new Intent(this,com.Services.PollingService.class));        
 */
    }
    @Override
    public void  onResume(){
    	super.onResume();
    	
        this.registerReceiver(this.mBR, mConsoleIf);
        if(!PollingService.isRunning()){
        	this.startService(new Intent(this,com.Services.PollingService.class));        
        }     	
        
        mAdapter.open();
        mAdapter.purgeAll();//testing
        mAdapter.loadTestData();
    }
    @Override
    public void onPause(){//called when new activity started
    	super.onPause();
    	//this.unregisterReceiver(consoleBR);
    this.unregisterReceiver(this.mBR);
    }
    @Override
    public void onStop(){
    	super.onStop();

    }
    @Override
    public void onDestroy(){
    	//this.unregisterReceiver(receiver)
    	super.onDestroy();
    	if(PollingService.isRunning()){
    		this.stopService(new Intent(this,com.Services.PollingService.class));
    	}
    	mAdapter.close();
    }
    ////////////////Helper function
    
    public void updateGUI(String newValue){
    	//TextView status = (TextView) findViewById(R.id.StatusText);
		//Simulated update GUI Algorithm
        //	status.setText("Status: "+"\n"+newValue);
	}
    
    ///----------Actions
    
    
    protected void toggleActivities(){
    	Intent temp = new Intent(this,act.QDF.DebugConsole.class);
		temp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	//this.stopService(new Intent(this,com.Services.PollingService.class));
		startActivity(temp);
		//this.finish();
		}
    
    
       //Listeners 
    private OnClickListener mUpdateSettingsListener = new OnClickListener() {
        public void onClick(View v) {
        	//adapter.updateData();
        }
    };
    private OnClickListener mToggleListener = new OnClickListener() {
        public void onClick(View v) {
        	toggleActivities();
        }
    };
    public class TimeOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          //Toast.makeText(parent.getContext()), "The planet is " +
              TimeScale = parent.getItemAtPosition(pos).toString();//, Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }//Listenr
    public class FreqOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          //Toast.makeText(parent.getContext()), "The planet is " +
              FreqScale=parent.getItemAtPosition(pos).toString();//, Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }//Listenr
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
    	        String results = "Fail";
    			/*
    			Cursor cursor = (SQLiteCursor) mAdapter.readData();//FIXME
    	        cursor.moveToLast();
    	        results = "--------New Record--------\n" +
    	        		//"ID: "+cursor.getString(0)+
    	        		"\nTime: "+ new Timestamp(Long.parseLong(cursor.getString(0))).toGMTString()+
    	        		"\nLocation: "+cursor.getString(1);
    	        cursor.close();
    	            			*/
    			return results;

    		}

    		@Override
    		public void onPostExecute(String result){
    			updateGUI(result);
    			
    		}
    }//Asynch Task
}
