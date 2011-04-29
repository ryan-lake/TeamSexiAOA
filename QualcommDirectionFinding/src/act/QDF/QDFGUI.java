package act.QDF;

import java.sql.Timestamp;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;
import com.Services.PollingService;

import act.QDF.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
///////////////////////////////////////////////////////
/*
/////////////////////////////////////////////////////////////////////
         ___           ___           ___           ___      
        /\  \         /\  \         /\  \         /\__\    
        \:\  \       /::\  \       /::\  \       /::|  |   
         \:\  \     /:/\:\  \     /:/\:\  \     /:|:|  |   
         /::\  \   /::\~\:\  \   /::\~\:\  \   /:/|:|__|__ 
        /:/\:\__\ /:/\:\ \:\__\ /:/\:\ \:\__\ /:/ |::::\__\
       /:/  \/__/ \:\~\:\ \/__/ \/__\:\/:/  / \/__/~~/:/  /
      /:/  /       \:\ \:\__\        \::/  /        /:/  / 
      \/__/         \:\ \/__/        /:/  /        /:/  /  
                     \:\__\         /:/  /        /:/  /   
                      \/__/         \/__/         \/__/    
         ___           ___           ___                 
        /\  \         /\  \         |\__\          ___   
       /::\  \       /::\  \        |:|  |        /\  \  
      /:/\ \  \     /:/\:\  \       |:|  |        \:\  \ 
     _\:\~\ \  \   /::\~\:\  \      |:|__|__      /::\__\
    /\ \:\ \ \__\ /:/\:\ \:\__\ ____/::::\__\  __/:/\/__/
    \:\ \:\ \/__/ \:\~\:\ \/__/ \::::/~~/~    /\/:/  /   
     \:\ \:\__\    \:\ \:\__\    ~~|:|~~|     \::/__/    
      \:\/:/  /     \:\ \/__/      |:|  |      \:\__\    
       \::/  /       \:\__\        |:|  |       \/__/    
        \/__/         \/__/         \|__|                

Students Engineering Xtreme Interfaces (S.E.X.I)
/////////////////////////////////////////////////////////////////////
*/
/**
 * Qualcomm Direction Finding(QDF) Application-
 * 
 * 
 */
///////////////////////////////////////////////////
public class QDFGUI extends Activity {

    public static final String POLLINGACTION = "act.QDF.QDFGUI.Polling";
    public static final String UPDATEACTION = "act.QDF.QDFGUI.UpdateSettingsDone";
    public static final String TOGTOCONACTION = "act.QDF.QDFGUI.ToggleToConsole";
    
    public static final String QDFTAG = "QDF";
	
    private BroadcastReceiver mBR;
	IntentFilter mConsoleIf;
	
    //Selections from the Drop downs
	int mFreq; 
	int mTime;
    String mFreqScale;
    String mTimeScale;
    
    //DB    
    QDFDbAdapter mAdapter;
    int mDegree;
    int mCurrentPowerLevel;
    String mDirection;
    
    int mMaxPower;//NEED TO GET
        
    //Object ID of the current Active Radio button
    UIUpdateTask updateTask;
    int intID;
   
    //Progress Dialog-used when updating the settings
    ProgressDialog mProgress;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qdfgui); 
      
        mConsoleIf = new IntentFilter();
        mConsoleIf.addAction(this.POLLINGACTION);
        mConsoleIf.addAction(this.UPDATEACTION);       
        
        mBR = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
        		//TextView status = (TextView) findViewById(R.id.StatusText);        		
            	String action =  intent.getAction();
            	
            	if(QDFGUI.UPDATEACTION.equals(action))
            		{mProgress.dismiss();}
            	else if(QDFGUI.POLLINGACTION.equals(action)){          		
            		if(updateTask==null||updateTask.getStatus().toString()==Status.FINISHED.toString()){
            		 //new UIUpdateTask().execute();
            		updateTask = new UIUpdateTask();
            		updateTask.execute();
            			Log.i("QDF","New AsyncTask Started");
            		}
            		else{
            			Log.i("QDF","Still processing GUI request");
            		
            		}
            			
            	}          	
            }
        };
        
        //Load default values
        mDegree = 0;//default
        this.mCurrentPowerLevel = 0;
        mMaxPower = 1000;
        intID = R.id.radioButtonE;//Default
      
        mFreqScale = "MHz";
        mTimeScale = "Sec";
        
        //DB
        mAdapter = new QDFDbAdapter(this);
        
        //Update Setting progress dialog 
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Changeing setting for Angle of Arrival...");
        mProgress.setCancelable(false);
                
        /*Spinner - Time*/
        Spinner spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> adapterTime = ArrayAdapter.createFromResource(
                this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapterTime);
        spinnerTime.setOnItemSelectedListener(TimeOnItemSelectedListener);
        
        /*Spinner - Freq*/
        Spinner spinnerFreq = (Spinner) findViewById(R.id.spinnerFreq);
        ArrayAdapter<CharSequence> adapterFreq = ArrayAdapter.createFromResource(
                this, R.array.freq_array, android.R.layout.simple_spinner_item);
        adapterFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFreq.setAdapter(adapterFreq);       
        spinnerFreq.setOnItemSelectedListener(FreqOnItemSelectedListener);
        
        
        //Buttons
        Button toggleToConButton = (Button)findViewById(R.id.toggleToConButton);        
        toggleToConButton.setOnClickListener(mToggleListener);
        
        Button updateSettingsButton = (Button)findViewById(R.id.updateSettingsButton);        
        updateSettingsButton.setOnClickListener(mUpdateSettingsListener);
        
        //Power Bar
        SeekBar powerBar = (SeekBar)findViewById(R.id.powerBar);
        powerBar.setIndeterminate(false);
        
        /*FIXME Set up Keyboard to actually listen
         * 
         */
       //EditText timeText = (EditText)findViewById(R.id.editTextTime);
        //timeText.set
        
       // EditText freqText = (EditText)findViewById(R.id.editTextFreq);     
        
        
        //Pull settings from the GUI and  
        setSettingsValues();
        
        Log.i(QDFTAG, "QDF Initialized.");
        
	}//OnCreate
	  
    @Override
    public void onStart(){
    	super.onStart();	
    }
    @Override
    public void  onResume(){
    	super.onResume();
    	
        this.registerReceiver(this.mBR, mConsoleIf);
        
        if(!PollingService.isRunning()){
        	this.startService(new Intent(this,com.Services.PollingService.class));        
        } 
        
        
        
        /*Set up the Database*/
        mAdapter.open();
        //Plan is that the GUI will not begin receiving data
        //till the Settings table is populated and then the C/C++ scripts will fire
        //Then data should start coming in
        mAdapter.purgeAll();
       
        mAdapter.initializeSettings(); 
        
        mProgress.show();//lock GUI till the handshake is done
        Log.i(QDFTAG, "QDF Resumed.");
        
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
    	if(PollingService.isRunning()){
    		this.stopService(new Intent(this,com.Services.PollingService.class));
    	}

    	Log.i(QDFTAG,"Attempting to Stop GUI task: "+this.updateTask.cancel(true));
    	mAdapter.close();
        Log.i(QDFTAG, "QDF Stoped.");
    	mProgress.dismiss();

    }
    @Override
    public void onDestroy(){
    	//this.unregisterReceiver(receiver)
    	super.onDestroy();
    	
    }
    ////////////////Helper function
    
    public void updateGUI(String newValue){   	
    	//Turn on the corresponding radio button based on the feed back we got formt he functions  	
    	if(newValue.equals("BadCursor"))//do nothing if DB value is bad
    		return;
    	
    	if(intID!=-1){
    		RadioButton oldButton = (RadioButton)findViewById(intID);        
    		oldButton.setChecked(false);
    	}
    	
    	int index = newValue.indexOf(',');// substring(start)
    	mDirection = newValue.substring(0, index);
    	intID = Integer.parseInt(newValue.substring(index+1));
    	
        RadioButton newButton = (RadioButton)findViewById(intID);        
        newButton.setChecked(true);
        //newButton.set
        //newButton.setSelected(true);
        EditText degreeText = (EditText)findViewById(R.id.editTextDegree);        
        degreeText.setText("Degree: "+mDegree);
        
        EditText dirText = (EditText)findViewById(R.id.editTextDirection);        
        dirText.setText("Dir.: "+mDirection);
        
        //Set Max at 100 thus all we need to do is set the percentage
        SeekBar powerBar = (SeekBar)findViewById(R.id.powerBar);
        
        powerBar.setProgress((mCurrentPowerLevel*100)/mMaxPower);
        
        //FIXME - kill only the old value
        mAdapter.purgeData();
        
    }

    ///----------Actions       
    protected void toggleActivities(){
    	Intent temp = new Intent(this,act.QDF.DebugConsole.class);
		temp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	//this.stopService(new Intent(this,com.Services.PollingService.class));
		startActivity(temp);
		//this.finish
		}
    
    
//Listeners 
    private OnClickListener mUpdateSettingsListener = new OnClickListener() {
        public void onClick(View v) {
        	//ProgressDialog dialog = ProgressDialog.show(thisOne, "", "Changing ");//TODO Cancelable listener?
        	//dailog.
        	//
        	
        	//mAdapter.purgeSettings();
        	//mAdapter.loadTestData();
        	mAdapter.purgeSettings();
        	mProgress.show();
        	setSettingsValues();
        	mAdapter.updateSettings(mTime, mFreq);

        }
    };
    private OnClickListener mToggleListener = new OnClickListener() {
        public void onClick(View v) {
        	toggleActivities();
        }
    };
    
    public OnItemSelectedListener TimeOnItemSelectedListener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
          //Toast.makeText(parent.getContext()), "The planet is " +
              mTimeScale = parent.getItemAtPosition(pos).toString();
              
        }
        public void onNothingSelected(AdapterView parent) {
        	// Do nothing.
        }
    };
    public OnItemSelectedListener FreqOnItemSelectedListener = new OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
              mFreqScale=parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView parent) {
        	// Do nothing.
        }
    };
    
    //Update GUI
    //Change the edit box based one he new scale
    //public  {}
    
   //Set the global Settings 
    //If the edit box is chaged fire this to update the new settings 
   // public {}
    /**
     * 
     * Get values from the GUI edit text boxes and scale the values and store them
     * 
     */
     
 private void setSettingsValues(){
  Float temp;
  float temp2;

  		//Get Freq value from the Editable text box
       EditText freqText = (EditText)findViewById(R.id.editTextFreq);       
       temp2 = Float.parseFloat(freqText.getText().toString());
       
       if(mFreqScale.equals("MHz")){
    	   temp = new Float(temp2*1000000);
    	   mFreq= temp.intValue();
       }else if(mFreqScale.equals("KHz")){
    	   temp = new Float(temp2*1000);
    	   mFreq= temp.intValue();
       }else if(mFreqScale.equals("Hz")){
    	   temp = new Float(temp2);
    	   mFreq= temp.intValue();
       }

       //Get time value from editable text
       EditText timeText = (EditText)findViewById(R.id.editTextTime);
       temp2 = Float.parseFloat(timeText.getText().toString());
       
       if(mTimeScale.equals("Sec")){
    	   temp = new Float(temp2*1000);
    	   mTime= temp.intValue();
       }else if(mTimeScale.equals("mSec")){
    	   temp = new Float(temp2);
    	   mTime= temp.intValue();
       }
       
       
    }
    /**
     * Do calculations to update the GUI, determine degrees, and active element
     *
     *NOTE: We have have approximately 5 instances of this thread spawned at any moment in time
     */
    public class UIUpdateTask extends AsyncTask<CharSequence,Void, String>{
    		public UIUpdateTask() {
    			super();
    		}
    		
    		@Override
       		protected String doInBackground(CharSequence... arg0) {
    	        /*
    	         * 
    	         * True directions:
    	         * 
    	         * East = 0
    	         * North = 90
    	         * West = 180
    	         * South = 270
    	         * 
    	         * 16 buttons
    	         *     			
    			 * 16 possible states depending on the degree	
    			 * E
    			 * ENE
    			 * NE
    			 * NNE
    			 * N
    			 * NNW
    			 * NW
    			 * WNW
    			 * W
    			 * WSW
    			 * SW
    			 * SSW
    			 * S
    			 * SSE
    			 * SE
    			 * ESE
    			 *	
    			 *
    			 * 
    	         * 360 Degress total/16=22.5 Degrees per button
    	         * *+or- 11.25
    	         * 
    	         * Thus the primary function of this is to return the
    	         *  coordinate/state we are in. This returns the ID
    	         */
    			String results = "N";//represents the state, Defualt to north 
    			int intID = R.id.radioButtonN;
	        	mDegree = 90;
	        	mCurrentPowerLevel = 0;
				
    			
    			//FIXME-better try catch support
    			Cursor cursor;
    			try{
    				cursor = (SQLiteCursor) mAdapter.readData();
    				cursor.moveToLast();
    				if(cursor.getCount()==0){
    					return "BadCursor";
    				}
        			mDegree=Integer.parseInt(cursor.getString(1));
        			mCurrentPowerLevel = Integer.parseInt(cursor.getString(2));
    	        	cursor.close();
    			}catch(Exception e){
    				//FIXME errors occuring cause we are nuking the Database every time
    				Log.e(QDFTAG, "Problem with Cursor\n");
    	        	mDegree = 90;
    	        	mCurrentPowerLevel = 0;
    				
    				cursor=null;
    			}
    	            	        
    			//Enforce degree range
    			if(mDegree>360 || mDegree<0)
    				{
    				return results+","+String.valueOf(intID);}
//Determine what element/state is active
      			if(mDegree >=348.7||mDegree<11.25)
      				{
      				 results = "E";
      				 intID = R.id.radioButtonE;}
      			else if(mDegree >=11.25&&mDegree<33.75)
  					{results = "ENE";
  					intID = R.id.radioButtonENE;}
      			else if(mDegree >=33.75&&mDegree<56.25)
					{results = "NE";
					intID = R.id.radioButtonNE;}      				
      			else if(mDegree >=56.25&&mDegree<78.75)
					{results = "NNE";
					intID = R.id.radioButtonNNE;}
      			else if(mDegree >=78.75&&mDegree<101.25)
					{results = "N";
					intID = R.id.radioButtonN;}
      			else if(mDegree >=101.25&&mDegree<123.75)
					{results = "NNW";
					intID = R.id.radioButtonNNW;}
      			else if(mDegree >=123.75&&mDegree<146.25)
					{results = "NW";
					intID = R.id.radioButtonNW;}
      			else if(mDegree >=146.25&&mDegree<168.75)
					{results = "WNW";
					intID = R.id.radioButtonWNW;}
      			else if(mDegree >=168.75&&mDegree<191.25)
					{results = "W";
					intID = R.id.radioButtonW;}
      			else if(mDegree >=191.25&&mDegree<213.75)
					{results = "WSW";
					intID = R.id.radioButtonWSW;}
      			else if(mDegree >=213.75&&mDegree<236.25)
					{results = "SW";
					intID = R.id.radioButtonSW;}
      			else if(mDegree >=236.25&&mDegree<258.75)
					{results = "SSW";
					intID = R.id.radioButtonSSW;}
      			else if(mDegree >=258.75&&mDegree<281.25)
      				{results = "S";
      				intID = R.id.radioButtonS;}
      			else if(mDegree >=281.25&&mDegree<303.75)
      				{results = "SSE";
      				intID = R.id.radioButtonSSE;}
      			else if(mDegree >=303.75&&mDegree<326.25)
      				{results = "SE";
      				intID = R.id.radioButtonSE;}
      			else if(mDegree >=326.25&&mDegree<348.75)
      				{results = "ESE";
      				intID = R.id.radioButtonESE;}
    			//Now we have state
      			
      			return results+","+String.valueOf(intID);
    		}
    		@Override
    		public void onPostExecute(String result){
    			updateGUI(result);  
    			//this.cancel(true);

    			/*try {
					this.finalize();
				} catch (Throwable e) {
					Log.e("QDFGUI", e.getMessage());
				}	
				*/		
    		}
    		
    }//Asynch Task
}//QDF Activity
