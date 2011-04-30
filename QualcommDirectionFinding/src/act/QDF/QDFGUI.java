package act.QDF;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;
import com.Services.PollingService;

import act.QDF.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    
    static int mDegree;
    static int mCurrentPowerLevel;
    static String mDirection;
    
    int mMaxPower;//NEED TO GET
        
    //Object ID of the current Active Radio button
    //UIUpdateTask updateTask;
    
    static int oldIntID;
    static int newIntID;
   
    //Progress Dialog-used when updating the settings
    ProgressDialog mProgress;
    Dialog mAbout;
    
    //String of the current GUI state
   private static boolean todoUpdate;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qdfgui); 
      
        mConsoleIf = new IntentFilter();
        mConsoleIf.addAction(QDFGUI.POLLINGACTION);
        mConsoleIf.addAction(QDFGUI.UPDATEACTION); 
        
        mBR = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
        		//TextView status = (TextView) findViewById(R.id.StatusText);        		
            	String action =  intent.getAction();
            	
            	if(QDFGUI.UPDATEACTION.equals(action))
            		{mProgress.dismiss();}
            	else if(QDFGUI.POLLINGACTION.equals(action)){          		
            		if(QDFGUI.todoUpdate==true){
            			updateGUI();//Designed to be used with he Intent service
            			QDFGUI.todoUpdate=false;
            		}
            	}
            }
        };
        //no updates to do
        todoUpdate = false;
        
        //Load default values
        QDFGUI.mDegree = 0;//default
        QDFGUI.mCurrentPowerLevel = 0;
        mMaxPower = 1000;
        QDFGUI.oldIntID = R.id.radioButtonE;//Default
      
        mFreqScale = "MHz";
        mTimeScale = "Sec";
        
        //DB
        mAdapter = new QDFDbAdapter(this);
        
        //Update Setting progress dialog 
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Changeing setting for Angle of Arrival...");
        mProgress.setCancelable(false);
        
        //About dialog
        mAbout = new Dialog(this);

        mAbout.setContentView(R.layout.about);
        mAbout.setTitle("Qualcomm- Team S.E.X.I.");

        TextView text = (TextView) mAbout.findViewById(R.id.text);
        text.setText("Students Engineering Xtreme Interfaces\n"+
        		"Qualcomm Sponsor: Dan Willis\n"+
        		"Project Manager: Ryan Lake\n\n" +
        		"Antenna Designer: Liza Resley\n"+
        		"Software/embedded: Ryan Lake\n"+
        		"RF/Software Designer: Brad Lovell\n"+
        		"Application Engineer: Matthew White");
        
        ImageView image = (ImageView) mAbout.findViewById(R.id.image);
        image.setImageResource(R.drawable.quallogo);
        
        Button doneButton = (Button)mAbout.findViewById(R.id.doneButton);        
        doneButton.setOnClickListener(this.mCloseAboutListener);
        
                
        
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
        Button aboutButton = (Button)findViewById(R.id.aboutButton);        
        aboutButton.setOnClickListener(mOpenAboutListener);
        
        Button updateSettingsButton = (Button)findViewById(R.id.updateButton);        
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

    	//Log.i(QDFTAG,"Attempting to Stop GUI task: "+this.updateTask.cancel(true));
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
    public void updateGUI(){   	
    	//Turn on the corresponding radio button based on the feed back we got formt he functions  	
    	
    	if(oldIntID!=-1){
    		RadioButton oldButton = (RadioButton)findViewById(oldIntID);        
    		oldButton.setChecked(false);//FIXME NPE?
    	}
    	    	
        RadioButton newButton = (RadioButton)findViewById(newIntID);        
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
    }

    ///----------Actions       
  //  public void displayAbout(){
    		
//		}
    
    //QDFGUI.setUpdate(results,intID,mDegree,mCurrentPowerLevel)
    public static void setUpdate(String direction,int newIntID,int degree,int powerLevel){
        QDFGUI.mDegree = degree;
        
        QDFGUI.mCurrentPowerLevel=powerLevel;
        QDFGUI.mDirection = direction;

        QDFGUI.oldIntID = QDFGUI.newIntID;
        QDFGUI.newIntID = newIntID;
    	
    	QDFGUI.todoUpdate=true;
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
    private OnClickListener mOpenAboutListener = new OnClickListener() {
        public void onClick(View v) {
        	mAbout.show();
        }
    };
    private OnClickListener mCloseAboutListener = new OnClickListener() {
        public void onClick(View v) {
        	mAbout.dismiss();
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
}//QDF Activity
