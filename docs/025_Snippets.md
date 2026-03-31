
# Snippets
This page lists useful code snippets to copy & paste instead of having to search for them in the depths of the documentation:

### General 
**Execute:** Execute a test with maven:

```
mvn clean verify -Dpfr_test="com.performetriks.performator.quickstart.tests.basics.PFRTestExample"
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

**Ranged Metric - for Record:** Make a ranged metric for a record that has been ended:

``` java
HSRRecord record = HSR.end();
int count = <yourCount>;

HSR.addMetricRanged( record, " - #Count", count, 5);
HSR.addMetricRangedWithSLA(record, " - #Count", count, 5);
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
PFRHttp.create(...).measureSize(ByteSize.KB)

// or with custom name
BigDecimal size = r.getBodySize(ByteSize.KB);
HSR.addMetricRanged(
  r.getName() + " - Response(KB)"
  , new BigDecimal(r.getDuration())
  , size.intValue()
  , 5
);
```

### JsonPath (JayWay)

Example data:

```javascript
{
	"success": true,
	"message": "successful",
	"count": 5,
	"payload": 
	[
	  { "id": 0, "firstname": "Aurora",   "status": "NEW",       "balance": 67.7 },
	  { "id": 1, "firstname": "Hera",     "status": "OPEN",      "balance": 91.3 },
	  { "id": 2, "firstname": "Rhea",     "status": "CLOSED",    "balance": 57.8 },
	  { "id": 3, "firstname": "Zeus",     "status": "CLOSED",    "balance": 113.4},
	  { "id": 4, "firstname": "Hercules", "status": "CANCELLED", "balance": 129.1}	
	]
}
```

Example Queries:

| JsonPath                                | Result                                                       |
|:----------------------------------------|:-------------------------------------------------------------|
| Boolean                                 | `Boolean success      = ctx.read("$.success");`              |
| String                                  | `String message       = ctx.read("$.message");`              |
| Integer                                 | `Integer count        = ctx.read("$.count");`                |
| Array: Length                           | `Integer count        = ctx.read("$.payload.length()");`     |
| Array: First ID                         | `Integer id           = ctx.read("$.payload[0].id");`        |
| Array: Last ID                          | `Integer id           = ctx.read("$.payload[-1].id");`       |
| Array: All IDs                          | `List<Integer> IDs    = ctx.read("$.payload[*].id");`        |
| Array: All IDs with balance > 100       | `List<Integer> IDs    = ctx.read("$.payload[?(@.balance > 100)].id");`        |
| Array: All IDs in status NEW or OPEN    | `List<Integer> IDs    = ctx.read("$.payload[?(@.status == 'NEW' || @.status == 'OPEN')].id");`        |



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



