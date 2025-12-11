package com.performetriks.performator.quickstart.usecase;

import com.google.gson.JsonArray;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.data.PFRDataRecord;
import com.performetriks.performator.data.PFRDataSource;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.http.PFRHttpResponse;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.globals.Globals.DATAFIELDS;

public class UsecaseCheckTestdata extends PFRUsecase {

	@Override
	public void initializeUser() {}
	
	/************************************************************************
	 * This example shows you how to use test data in a JSON String.
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
		
			//=======================================
			// Get Testdata
			PFRDataRecord record = Globals.DATA.next();

			int index 			= record.get(DATAFIELDS.INDEX).getAsInteger();
			String username 	= record.get(DATAFIELDS.USER).getAsString();
			String firstname 	= record.get(DATAFIELDS.FIRSTNAME).getAsString();
			String lastname 	= record.get(DATAFIELDS.LASTNAME).getAsString();
			String location 	= record.get(DATAFIELDS.LOCATION).getAsString();
			boolean tiramisu	= record.get(DATAFIELDS.LIKES_TIRAMISU).getAsBoolean();
			
			//=======================================
			// 
			
			PFRHttpResponse r = PFRHttp.create("CheckData", "https://jsonplaceholder.typicode.com/posts") 
					.POST()
					.bodyJSON("""
						{
						      "index": %d
						    , "username": "%s"
						    , "firstname": "%s"
						    , "lastname": "%s"
						    , "location": "%s"
						    , "tiramisu": %b
						}		
						""".formatted(index, username, firstname, lastname, location, tiramisu)
						// >> How to use string format: https://www.w3schools.com/java/ref_string_format.asp
					)
					.checkBodyContains("\"tiramisu\": true")
					.pause(100)
					.send()
					;
			
			//=======================================
			// Check if response is fine
			if( ! r.isSuccess() ) {

				System.out.println("=========== PROBLEMATIC RECORD ============");
				System.out.println("Record: "+ record.toString());
				System.out.println("Response: "+ r.getBody());
			}
			

	}

	@Override
	public void terminate() {}
	


}
