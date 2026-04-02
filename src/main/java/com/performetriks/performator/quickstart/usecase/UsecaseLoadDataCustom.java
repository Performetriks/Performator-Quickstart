package com.performetriks.performator.quickstart.usecase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRUsecase;
import com.xresch.xrutils.data.Unrecord;
import com.performetriks.performator.data.PFRDataSource;
import com.xresch.xrutils.utils.XRTimeUnit;

public class UsecaseLoadDataCustom extends PFRUsecase {

	JsonArray customData = new JsonArray();
	
	/************************************************************************
	 * Here is an example on how you can create custom random data.
	 ************************************************************************/
	@Override
	public void initializeUser() {
				
		
		for(int i = 0 ; i < 100; i++) {
			
			//--------------------------------------------
			// Basic Person Data
			String firstname = PFR.Random.firstnameOfGod();
			String lastname = PFR.Random.lastnameSweden();
			String location = PFR.Random.mythicalLocation();
			
			//--------------------------------------------
			// Birthday and age between 18 and 100
			long birthdayMillis = PFR.Random.longInRange(XRTimeUnit.y.offset(null, -100), XRTimeUnit.y.offset(null, -18));
			String birthday = PFR.Time.formatMillis(birthdayMillis, "YYYY-MM-dd");
			int age = (int)Math.ceil( XRTimeUnit.y.difference(birthdayMillis, System.currentTimeMillis()) );
			
			//--------------------------------------------
			// Country Data
			JsonObject countryData = PFR.Random.countryData();
			String country = countryData.get("Country").getAsString();
			String countryCode = countryData.get("CountryCode").getAsString();
			String capital = countryData.get("Capital").getAsString();
			
			//--------------------------------------------
			// Username And Email
			String username = (firstname.charAt(0) +"."+ lastname).toLowerCase();
			String email = (firstname +"."+ lastname + "@" + location.replace(" ", "-") + "." +countryCode).toLowerCase();

			//--------------------------------------------
			// Address
			JsonObject address = new JsonObject();
			address.addProperty("street", PFR.Random.street());
			address.addProperty("city", capital);
			address.addProperty("zipcode", PFR.Random.integer(10000, 99999));
			address.addProperty("country", country);
			
			//------------------------
			// Person Object
			JsonObject object = new JsonObject();
			
			object.addProperty("id", i);
			object.addProperty("username", username);
			object.addProperty("firstname", firstname);
			object.addProperty("lastname", lastname);
			object.addProperty("email", email);
			object.addProperty("birthday", birthday);
			object.addProperty("age", age);
			object.addProperty("active", PFR.Random.bool());
			object.addProperty("orders", PFR.Random.integer(0, 42));
			object.addProperty("score", PFR.Random.bigDecimal(33, 100, 1));
			object.add("address", address);
			
			//------------------------
			// Add To Array
			customData.add(object);
		}
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
					

			PFRDataSource userData = PFR.Data.newSourceJsonArray("customData", customData)
											.build();
			
			// print a few records
			for(int i = 0; i <= 3; i++) {
				Unrecord record = userData.next();
				System.out.println("========================");
				System.out.println(record.toStringPretty());
			}

	}

	@Override
	public void terminate() {}
	


}
