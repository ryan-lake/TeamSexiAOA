package com.Services;

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
		process =  new PollProcess();
		process.parent = this;
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
		process.allDone=true;
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
		public boolean allDone;
		public PollingService parent;
		
		public void run() {
			while(!allDone){
				if(USRPVectorsFrame.buffReady){

					//allDone = true;//????
					//Intent temp = new Intent(parent,act.DebugConsole.DebugConsole.ConsoleBR.class);
//					Intent temp = new Intent();
//					temp.setAction(DebugConsole.POLLINGACTION);
//					temp.setComponent(new ComponentName("act.DebugConsole.DebugConsole","ConsoleBR"));//works :-)
//-------------------------
					Intent temp1 = new Intent();
					temp1.setAction(DebugConsole.POLLINGACTION);				
//					temp1.setComponent(new ComponentName("act.DebugConsole","ConsoleBRStatic"));
//-------------------------				
//					Intent temp3 = new Intent("act.DebugConsole.DebugConsole.ConsoleBR");
//					temp3.setAction(DebugConsole.ACTION);				
//					//temp3.setComponent(new ComponentName("act.DebugConsole","ConsoleBRStatic"));
//-------------------------					

					//parent.sendBroadcast(temp1);
//					temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					temp1.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
//					temp2.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
//					temp3.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
					
//					parent.sendBroadcast(temp);
					parent.sendBroadcast(temp1);
//					parent.sendBroadcast(temp2);
//					parent.sendBroadcast(temp3);
					
					USRPVectorsFrame.buffReady =false;
					//WORKS

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

		
	
	}
	
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

