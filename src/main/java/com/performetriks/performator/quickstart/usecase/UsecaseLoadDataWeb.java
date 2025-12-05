package com.performetriks.performator.quickstart.usecase;

import com.google.gson.JsonArray;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.data.PFRDataRecord;
import com.performetriks.performator.data.PFRDataSource;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.http.PFRHttpResponse;

public class UsecaseLoadDataWeb extends PFRUsecase {

	@Override
	public void initializeUser() {}
	
	/************************************************************************
	 * This example shows you how to load data from a web source.
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
		
							
			//=======================================
			// 
			PFRHttpResponse r = PFRHttp.create("LoadTypicodeData", "https://jsonplaceholder.typicode.com/users") 
					.checkBodyContains("\"username\":")
					.send()
					.throwOnFail()
					;
			
			//=======================================
			// Load data
			JsonArray userArray = r.getBodyAsJsonArray();
			
			PFRDataSource userData = PFR.Data.newSourceJsonArray("userList", userArray)
											.build();
			
			PFRDataRecord record = userData.next();
			System.out.println("========================");
			System.out.println("id:"+record.get("id").getAsInteger());
			System.out.println("username:"+record.get("username").getAsString());
			System.out.println("address:"+ PFR.JSON.toJSON( record.get("address").getAsJsonObject()) );
			System.out.println("All details:"+record.toString());

	}

	@Override
	public void terminate() {}
	


}
