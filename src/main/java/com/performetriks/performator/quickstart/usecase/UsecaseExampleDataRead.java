package com.performetriks.performator.quickstart.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.quickstart.globals.Globals;
import com.xresch.xrutils.data.XRRecord;


public class UsecaseExampleDataRead extends PFRUsecase {

	private static Logger logger = LoggerFactory.getLogger(UsecaseExampleDataRead.class.getName());
	
	private String url;
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void initializeUser() {

	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
		
		//=======================================
		// Load Test Data Record
		if( ! Globals.DATA.hasNext() ) {
			logger.warn(this.getName()+": No test data available.");
			return;
		}
		
		XRRecord record = Globals.DATA.next();
		
		//=======================================
		// Read Values
		String user 			= record.getString(Globals.DATAFIELDS.USER);
		int value 				= record.getInteger(Globals.DATAFIELDS.VALUE);
		boolean likesTiramisu 	= record.getBoolean(Globals.DATAFIELDS.LIKES_TIRAMISU);
		JsonObject objectData 	= record.getJsonObject(Globals.DATAFIELDS.OBJECT);
		JsonArray tags 			= record.getJsonArray(Globals.DATAFIELDS.TAGS);
		
		//=======================================
		// Log
		logger.info("======================================="); 
		logger.info("Record:        " + record.toString()); 
		logger.info("user:          " + user); 
		logger.info("value:         " + value); 
		logger.info("likesTiramisu: " + likesTiramisu); 
		logger.info("objectData:    " + PFR.JSON.toJSON(objectData)); 
		logger.info("tags:          " + PFR.JSON.toJSON(tags)); 

		
	}
	
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate() {
		// nothing todo
	}

}
