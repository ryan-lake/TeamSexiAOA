package com.Services;

import zAttempts.DBService;
import android.database.sqlite.SQLiteDatabase;

/**
 * Not 100% sure that this class is needed but it will give us 
 * the ability to truly only have 2 static instanace of the services and 
 * ,more importantly, one reference to the static DB
 * 
 * This would eliminate pass the DB around
 * 
 *  This also reinforces the "Modle View" paradiam
*/
public class ServiceManager {
	//public static PollingService pollingService;
		
	//public static UpdateDBService updateService;
	//not needed to be static, really could be replaced by just and Indent braocasting 
	//public static DBService databaseService;
	
	
	
}
