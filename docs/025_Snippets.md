
# Snippets
This page lists useful code snippets to copy & paste instead of having to search for them in the depths of the documentation:

### General 
**Execute:** Execute a test with maven:

```
mvn clean verify -Dpfr_test="com.performetriks.performator.quickstart.tests.PFRTestExample"
```

**Debug Logs:** Enable Debug logs:

``` java
// General
HSRConfig.setLogLevelRoot(Level.DEBUG);
HSRConfig.setLogLevel(Level.DEBUG, "com.performetriks.performator");
HSRConfig.setLogLevel(Level.DEBUG, "com.xresch.hsr");

// Http Plugin
PFRHttp.debugLogFail(true);
PFRHttp.debugLogAll(true);
```

### HTTP Plugin 
**HTTP Converter:** Starting the HTTP Converter of the Performator HTTP Plugin:

```java
mvn clean verify -Dpfr_mode=httpconverter
```



**HTTP - Ranged Metric: Json Array Count:** Get amount of items in the response and make a ranged metric:

``` java
int count = JsonPath.read(r.getBody(), "$.accounts.length()");
HSR.addMetricRanged(
  r.getName() + " - #Accounts"
  , new BigDecimal(r.getDuration())
  , count
  , 5
);
```

**HTTP - Ranged Metric: Response Size:** Make a ranged metric for the response size:

``` java
BigDecimal size = r.getBodySize(ByteSize.KB);
HSR.addMetricRanged(
  r.getName() + " - SizeKB"
  , new BigDecimal(r.getDuration())
  , size.intValue()
  , 5
);
```

### Generate Random Testdata

**Person Array:** Array of custom personal data:

```java

JsonArray customData = new JsonArray();

for(int i = 0 ; i < 100; i++) {
			
	//--------------------------------------------
	// Basic Person Data
	String firstname = PFR.Random.firstnameOfGod();
	String lastname = PFR.Random.lastnameSweden();
	String location = PFR.Random.mythicalLocation();
	
	//--------------------------------------------
	// Birthday and age between 18 and 100
	long birthdayMillis = PFR.Random.longInRange(HSRTimeUnit.y.offset(null, -100), HSRTimeUnit.y.offset(null, -18));
	String birthday = PFR.Time.formatMillis(birthdayMillis, "YYYY-MM-dd");
	int age = (int)Math.ceil( HSRTimeUnit.y.difference(birthdayMillis, System.currentTimeMillis()) );
	
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
	// Personal Data Object
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
```



