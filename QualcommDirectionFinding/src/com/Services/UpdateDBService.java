package com.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//Might be replaced since the SErvice manager and the DBservice 
//can handle the bulk of the broadcast events 
public class UpdateDBService extends Service {
	Thread updateThread;
	
	private static String name="updaterThread";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
