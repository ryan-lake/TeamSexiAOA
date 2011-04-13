package act.QDF;

import java.sql.Timestamp;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

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

public class QDFGUI extends Activity {

    public static final String POLLINGACTION = "act.QDF.QDFGUI.Polling";
    public static final String UPDATEACTION = "act.QDF.QDFGUI.Update";
    public static final String TOGTOCONACTION = "act.QDF.QDFGUI.ToggleToConsole";
    

	
    private BroadcastReceiver mBR;
	IntentFilter mConsoleIf;
	
    QDFDbAdapter mAdapter;
    
    
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
            
        mAdapter = new QDFDbAdapter(this);
        
        
    }//OnCreate
	  
    @Override
    public void onStart(){
    	super.onStart();	
/*        this.registerReceiver(mBR, mConsoleIf);
        
    	this.startService(new Intent(this,com.Services.PollingService.class));        
 */
    	
    	
       // adapter.open();
       // adapter.purgeAll();//testing
       // adapter.loadTestData();
       //this.registerReceiver(consoleBR, consoleIf);

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
    	//this.unregisterReceiver(consoleBR);
    	//adapter.close();
    	//this.stopService(new Intent(this,com.Services.PollingService.class));
    	//this.unregisterReceiver(this.yourReceiver);
    }
    @Override
    public void onDestroy(){
    	//this.unregisterReceiver(receiver)
    	super.onDestroy();
    	this.stopService(new Intent(this,com.Services.PollingService.class));
    	mAdapter.close();
    }
    @Override
    public void  onResume(){
    	super.onResume();
    	
        this.registerReceiver(this.mBR, mConsoleIf);
        try{
        this.startService(new Intent(this,com.Services.PollingService.class));        
        }catch(Exception e){
        	
        }
        mAdapter.open();
        mAdapter.purgeAll();//testing
        mAdapter.loadTestData();
    	
    
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
		this.finish();
		}
    
    
       //Listeners 
    private OnClickListener mUpdateListener = new OnClickListener() {
        public void onClick(View v) {
        	//adapter.updateData();
        }
    };
    private OnClickListener mToggleListener = new OnClickListener() {
        public void onClick(View v) {
        	toggleActivities();
        }
    };
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
    			
    			Cursor cursor = (SQLiteCursor) mAdapter.readData();//FIXME
    	        cursor.moveToLast();
    	        results = "--------New Record--------\n" +
    	        		//"ID: "+cursor.getString(0)+
    	        		"\nTime: "+ new Timestamp(Long.parseLong(cursor.getString(0))).toGMTString()+
    	        		"\nLocation: "+cursor.getString(1);
    	        cursor.close();
    			return results;
    			
    		}

    		@Override
    		public void onPostExecute(String result){
    			updateGUI(result);
    			
    		}
    }//Asynch Task
}
