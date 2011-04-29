package com.Services;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

import test.Data.TestControl;
import test.Data.USRPVectorsFrame;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import act.QDF.DebugConsole;
import act.QDF.QDFGUI;
/**
 * 
 * This Service should preform the basic operation of pooling a SQLite DB 
 *  to determine if there is a new entry in the DB
 * 
 * 
 * @author Matt White
 *
 */
public class PollingService extends Service {

	//PollingBinder mBinder = new PollingBinder();
	Thread pollThread;
	PollProcess process;
	
	private static boolean mRunning =false;
	

	private static String name="PollingServiceThread";
    
	public PollingService() {
		super();
	}
	
	public static boolean isRunning(){
		return mRunning;
	}
	
	private static void setRunning(boolean started){
		mRunning = started;
	}
	
	@Override
	public void onCreate(){
				
		process =  new PollProcess(this.getApplication());
		pollThread = new Thread(process);
		pollThread.setName(name);
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		//handleCommand(intent);
		setRunning(true);
			if(!pollThread.isAlive()){
				pollThread.start(); 
			}		
			/*
			try{
		}catch(Exception e){
			e.printStackTrace();//TODO Might need some better way to handle this
		}
	*/	
	    Log.i(QDFGUI.QDFTAG+name, "QDF Polling Service Started");
		return START_STICKY;
	}
	
	@Override	
	public void onDestroy(){//hmmmm??? need to test
		//kill the thread, once stopped null it to remove memmory footprint
		super.onDestroy();
		process.setAllDone(true);
		setRunning(false);
		/*
		do{
			if(pollThread.getState()==Thread.State.TERMINATED||pollThread.getState()==Thread.State.BLOCKED){
				pollThread = null;
			}
		}while(pollThread!=null);
		*/
        Log.i(QDFGUI.QDFTAG+name, "QDF Polling Service Stoped");

	}

	@Override
	/**
	 * Dosen't need to be bound thus it we are returning null
	 */
	public IBinder onBind(Intent arg0) {
		return null;
	}
	/**
	 * Now: Polling the static varible to see if it is time to pull new test data
	 * 
	 * Future: 
	 * 
	 *Intent set the Action and fire = best way, Specify the specific componet = better
	 *Set the Registered Recievers  only = awsome
	 */
	public class PollProcess implements Runnable{
		private boolean allDone;
		

		//private PollingService parent;//for intent handling
		
		private boolean status;
		private long timestamp;
	    
		private int oldRead;
	    private Application app;
		
	    public PollProcess(Application a){
	    	
	    	app = a;
	    	status = false;
	    	timestamp = 0;
	    	allDone = false;
	    }
	    
		public void run() {
			long newTimeStamp;
			int newRead;//0 = still not read, 1 = read
			int count = 0;//TODO Delete me
			while(!allDone){
				try{
				newTimeStamp = QDFDbAdapter.pollDataTable();
				newRead = QDFDbAdapter.pollSettingsTable();
				}catch(Exception e){
					newTimeStamp = 0;
					newRead= 0;//default to not being read
					Log.e(QDFGUI.QDFTAG+name, "Problem with database");
				}
				if(this.compareRead(newRead)){
					Intent temp = new Intent();
					temp.setAction(QDFGUI.UPDATEACTION);				
					app.sendBroadcast(temp);
				}
				if(timestamp == 0 && newTimeStamp != 0){
					//load default value but only if its a good value
					setTimestamp(newTimeStamp);
				}
				status = compareTimestamp(newTimeStamp);
				
				if(!status){//if new Data in the table
					setTimestamp(newTimeStamp);
					
					Intent temp = new Intent();
					temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
						temp.setAction(DebugConsole.POLLINGACTION);	
					app.sendBroadcast(temp);
						temp.setAction(QDFGUI.POLLINGACTION);
					app.sendBroadcast(temp);
				}
				//FIXME -REMOVE Simulated data handshake
				try{
				QDFDbAdapter.simHandShake(count);
				QDFDbAdapter.simFirstData(count);
				}catch(Exception e){
					this.allDone =true;
					Log.e("Polling Service", "Sim: FAIL");//Stop thread? then restart?
					Log.e("Polling Service", e.toString()==null?e.toString():"We Fail and dont know y");//Stop thread? then restart?
				}
				count++;
				//
				
				try{
					Thread.sleep(500);//1/2 second approximately
					}catch(Exception e){
						System.out.println("\nBroken: \n");
						e.printStackTrace();
					}

			}//while
			Log.i(QDFGUI.QDFTAG+name, "Polling service ended");
		}//run
/*
 * The transisiton form 0 to 1 is the important transition here
 */
	    private boolean compareRead(int newRead) {	
	    	boolean result = false;
	    	
	    	if (oldRead==0 && newRead==1){
	    			result = true;
	    	}
	    	oldRead=newRead;
	    	return result;
	    }
	    private boolean compareTimestamp(long timestamp) {	
	    	boolean result = false;
	    		if(this.timestamp == timestamp){
	    			result = true;
	    	}
	    	return result;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public boolean isAllDone() {
			return allDone;
		}
		public void setAllDone(boolean allDone) {
			this.allDone = allDone;
		}	
	}//Polling Process	
}//Polling service
