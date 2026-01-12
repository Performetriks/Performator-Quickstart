package com.performetriks.performator.quickstart.usecase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.data.PFRDataRecord;
import com.performetriks.performator.data.PFRDataSource;
import com.xresch.hsr.utils.HSRTime.HSRTimeUnit;

public class UsecaseLoadDataCustom extends PFRUsecase {

	JsonArray customData = new JsonArray();
	
	/************************************************************************
	 * Here is an example on how you can create custom random data.
	 ************************************************************************/
	@Override
	public void initializeUser() {
		
		
		for(int i = 0 ; i < 100; i++) {
			
			//------------------------
			// Create Data
			String firstname = PFR.Random.firstnameOfGod();
			String lastname = PFR.Random.lastnameSweden();
			String location = PFR.Random.mythicalLocation();
			
			//create birthday and age between 18 and 100
			long birthdayMillis = PFR.Random.longInRange(HSRTimeUnit.y.offset(null, -100), HSRTimeUnit.y.offset(null, -18));
			String birthday = PFR.Time.formatMillis(birthdayMillis, "YYYY-MM-dd");
			int age = (int)Math.ceil( HSRTimeUnit.y.difference(birthdayMillis, System.currentTimeMillis()) );
			
			JsonObject countryData = PFR.Random.countryData();
			String country = countryData.get("Country").getAsString();
			String countryCode = countryData.get("CountryCode").getAsString();
			String capital = countryData.get("Capital").getAsString();
			
			String username = (firstname.charAt(0) +"."+ lastname).toLowerCase();
			String email = (firstname +"."+ lastname + "@" + location.replace(" ", "-") + "." +countryCode).toLowerCase();

			JsonObject address = new JsonObject();
			address.addProperty("street", PFR.Random.street());
			address.addProperty("city", capital);
			address.addProperty("zipcode", PFR.Random.integer(10000, 99999));
			address.addProperty("country", country);
			
			//------------------------
			// Create Object
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
				PFRDataRecord record = userData.next();
				System.out.println("========================");
				System.out.println(record.toStringPretty());
			}

	}

	@Override
	public void terminate() {}
	


}
