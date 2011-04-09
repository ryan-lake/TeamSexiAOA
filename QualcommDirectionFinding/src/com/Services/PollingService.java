package com.Services;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

import test.Data.TestControl;
import test.Data.USRPVectorsFrame;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import act.DebugConsole.DebugConsole;
import act.DebugConsole.R;
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
	

	private static String name="PollingServiceThread";
    
	public PollingService() {
		super();
	}
	
	@Override
	public void onCreate(){
				
		process =  new PollProcess(this);
		pollThread = new Thread(process);
		pollThread.setName(name);
		

	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		//handleCommand(intent);
		pollThread.start();
		return START_STICKY;
	}
	
	@Override	
	public void onDestroy(){//hmmmm??? need to test
		//kill the thread, once stopped null it to remove memmory footprint
		process.setAllDone(true);
		do{
			if(pollThread.getState()==Thread.State.TERMINATED||pollThread.getState()==Thread.State.BLOCKED){
				pollThread = null;
			}
		}while(pollThread!=null);
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
		


		private PollingService parent;//for intent handling
		
		private boolean status;
	    private long timestamp;
		
	    public PollProcess(PollingService p){
	    	parent = p;//for intent handling
	    	status = false;
	    	timestamp = 0;
	    	allDone = false;
	    }
	    
		public void run() {
			while(!allDone){
				long newTimeStamp = QDFDbAdapter.pollDataTable();
				
				status = compareTimestamp(newTimeStamp);
				if(!status){
					setTimestamp(newTimeStamp);
					
					Intent temp = new Intent();
					temp.setAction(DebugConsole.POLLINGACTION);				
					temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					parent.sendBroadcast(temp);
				}			
				
				try{
					Thread.sleep(1000);//1 second
					}catch(Exception e){
						System.out.println("\nBroken: \n");
						e.printStackTrace();
					}
				
				/*
				if(TestControl.ready){
					Intent temp = new Intent();
					temp.setAction(DebugConsole.POLLINGACTION);				
					temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					parent.sendBroadcast(temp);

					TestControl.ready =false;
					//WORKS
					
					
					
				}else{
					try{
					Thread.sleep(1000);//1 second
					}catch(Exception e){
						System.out.println("\nBroken: \n");
						e.printStackTrace();
					}
				}//Ready
				*/
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
/*
class PollingThread extends Thread{
/*Due to the Depreciation of the Thread.stop(), Thread.destroy()
 *I've chosen to use a class boolean field to allow the run method 
 *to return safely.
 
	public boolean allDone;
	
	public PollingThread(""){
		 super();
		 allDone=false;
	 }
	@Override
	public void run() {
		while(!allDone){
			if(TESTVECTORS.buffReady){
				/** TODO Spawn, New Thread or AsynchTask for Algorithm calculations
				 *Create a new Algorithm service(AsynchTask) and spawn a thread with -in the
				 *service. When termnated called UPdate UI.
				 *
				 *OR
				 *
				 *Since AsyncTask, Fire Intent aimed at the Activity
				 *
				 *OR
				 *
				 *Bind new service to this service
				 *
				 *OR
				 *
				 *We just exit the thread... pick up where runable ends
				 /
				//allDone = true;//????
				

				//11:16am
				//
				//this.suspend();
				
			}else{
				try{
				Thread.sleep(1000);//1 second
				}catch(Exception e){
					System.out.println("\nFail: \n");
					e.printStackTrace();
				}
			}//bufferReady
		}//while
	}//run
	
	public boolean isAllDone() {	
		return allDone;
	}
	public void setAllDone(boolean allDone) {
		this.allDone = allDone;
	}
}//pollingThread
	*/
