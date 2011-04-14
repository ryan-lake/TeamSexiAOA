package com.Services;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

import test.Data.TestControl;
import test.Data.USRPVectorsFrame;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
			if(pollThread.isAlive()){
				//process.allDone= true; 
			}		
			else{
				pollThread.start();
			}
			/*
			try{
		}catch(Exception e){
			e.printStackTrace();//TODO Might need some better way to handle this
		}
	*/	
		return START_STICKY;
	}
	
	@Override	
	public void onDestroy(){//hmmmm??? need to test
		//kill the thread, once stopped null it to remove memmory footprint
		
		process.setAllDone(true);
		setRunning(false);
		/*
		do{
			if(pollThread.getState()==Thread.State.TERMINATED||pollThread.getState()==Thread.State.BLOCKED){
				pollThread = null;
			}
		}while(pollThread!=null);
		*/

	}

	@Override
	/**
	 * Dosen't need to be bound thus it we are returning null
	 */
	public IBinder onBind(Intent arg0) {
		return null;
	}
	//Redundent
	public void sendBroadcast(Intent intent){
		super.sendBroadcast(intent);
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
	    private Application app;
		
	    public PollProcess(Application a){
	    	
	    	app = a;
	    	status = false;
	    	timestamp = 0;
	    	allDone = false;
	    }
	    
		public void run() {
			while(!allDone){
				long newTimeStamp = QDFDbAdapter.pollDataTable();
				
				if(timestamp == 0 && newTimeStamp != 0){
					//load default value but only if its a good value
					setTimestamp(newTimeStamp);
				}
				status = compareTimestamp(newTimeStamp);
				
				if(!status){
					setTimestamp(newTimeStamp);
					
					Intent temp = new Intent();
					temp.setAction(DebugConsole.POLLINGACTION);	
					temp.setAction(QDFGUI.POLLINGACTION);
					
					temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					
					//if(app!=null){
						app.sendBroadcast(temp);
				}			
				
				try{
					Thread.sleep(500);//1 second
					}catch(Exception e){
						System.out.println("\nBroken: \n");
						e.printStackTrace();
					}

			}//while
		}//run

	    public boolean compareTimestamp(long timestamp) {	
	    	boolean result = false;
	    	if (this.timestamp!=0){
	    		if(this.timestamp == timestamp){
	    			result = true;
	    		}
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
