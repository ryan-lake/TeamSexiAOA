package com.Services;

import com.SQLiteDatabaseWrapper.QDFDbAdapter;

import act.QDF.QDFGUI;
import act.QDF.R;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.util.Log;

public class GUIUpdater extends IntentService {
	private static String UPDATETAG = "QDF GUIUpdater";
	
	public GUIUpdater() {
		  super("GUI Updater Service");
		}
	
	public GUIUpdater(String name) {
		super(name);

	}
	@Override
	public void onCreate(){
		super.onCreate();
		Log.i(UPDATETAG, "Intent Recieved");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		String results = "N";//represents the state, Default to north 
		int intID = R.id.radioButtonN;
    	
		int mDegree = 90;
    	int mCurrentPowerLevel = 0;
    	Cursor cursor = (SQLiteCursor) QDFDbAdapter.readData();
    	if(cursor!=null){
    	try{				    
				cursor.moveToLast();
				mDegree=Integer.parseInt(cursor.getString(1));
				mCurrentPowerLevel = Integer.parseInt(cursor.getString(2));
			}catch(Exception e){
				Log.e(UPDATETAG, "Problem with Cursor\n");
				mDegree = 90;
				mCurrentPowerLevel = 0;
			
				cursor=null;
		}
    	}
			if(cursor!=null)
				cursor.close();
    	       
		//Enforce degree range
		if(mDegree<=360 && mDegree>0)
			{		
//Determine what element/state should be active
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
			}

		QDFGUI.setUpdate(results,intID,mDegree,mCurrentPowerLevel);
		
		Intent temp = new Intent();
		temp.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		temp.setAction(QDFGUI.POLLINGACTION);
		this.sendBroadcast(temp);
	}

}
