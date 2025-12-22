# Data Sources
Data sources allow you to load data and use it in tests.

# Basics
Data sources extend from the abstract class `PFRDataSourceStatic`.
They can be created directly or using one of the methods `PFR.Data.newSource*()`.
Every data source has a unique name, which will be used for logging.
Every data source has two modes, an access mode and a retain mode.
The access mode defines in what order the data should be accessed, and the retain mode defines how often the same data should be used.

When creating a data source, you need to call the build()-method for the data to be loaded. Following an example:

```java
//--------------------------
// Load Test Data CSV	
PFRDataSource DATA = 
		PFR.Data.newSourceCSV("mainTestdataCSV", "com.mypackage.data", "testdata.csv", ",")
					.accessMode(AccessMode.SEQUENTIAL)
					.retainMode(RetainMode.ONCE)
					.build();
;
);
```

### RetainMode.ONCE and hasNext
Above will access a test data file and only uses every data record once in sequential order.
As the source might run out of data, you would need to check if it still has data as follows:

```java
		//=======================================
		// Load Test Data Record
		if( ! Globals.DATA.hasNext() ) {
			logger.warn(this.getName()+": No test data available.");
			return;
		}
		
		PFRDataRecord record = Globals.DATA.next();

);
```

### Accessing Data
After retrieving a PFRDataRecord, you can access the field values by name.
it will return a type of `Unvalue`, which allows you to flexibly retrieve a value as it's own type or as a different type(e.g. getting the string "4.44" as the number 4.44). It also enables us to load objects and arrays from Json, making the whole thing rather flexible.
There are as well various methods on the record to directly retrieve a specific type.

Here some examples on how to access the different types:

```java
Unvalue user Unvalue		= record.get("USER");
String user 					= record.getString("USER");
int value 					= record.getInteger("VALUE");
boolean likesTiramisu 		= record.getBoolean("LIKES_TIRAMISU");
JsonObject addressDetails 	= record.getJsonObject("ADDRESS_DETAILS");
JsonArray tags 				= record.getJsonArray("TAGS");
```


# Data Source CSV File
A data source that allows to load CSV data from a file in a java package:

```java
PFRDataSource DATA = 
		PFR.Data.newSourceCSV("mainTestdataCSV", "com.mypackage.data", "testdata.csv", ",")
					.accessMode(AccessMode.SEQUENTIAL)
					.retainMode(RetainMode.ONCE)
					.build();
```

The CSV parser used is quite lenient and tries to do whatever it cans to load the file.
It supports CSV with or without quoted values, or also mixed with some fields quoted and some not.

```java
"INDEX","ID","USER","FIRSTNAME","LASTNAME","LOCATION","EMAIL","LIKES_TIRAMISU","VALUE","SEARCH_FOR"
"0","aca90d7d-e59e-4c91-b17","u.bjoerk","Uranus","Bjoerk","Aztlan","u.bjoerk@aztlan.com","false","66","Gianduiotto"
"1","459d167e-b905-44ec-a85","r.viklund","Rhea","Viklund","Muspelheim","r.viklund@muspelheim.com","false","25","Panna Cotta"
"2","ff4c4746-dd96-405a-98e","e.wallin","Eros","Wallin","Laestrygon","e.wallin@laestrygon.it","","19","Taralli"
"3","018fb132-e63a-42d8-b3f","u.jansson","Uranus","Jansson","Kingdom of Reynes","u.jansson@kingdom-of-reynes.ch","true","7","Torrone"
"4","45909aea-ff23-40c8-b36","p.samuelsson","Pluto","Samuelsson","Asphodel Meadows","p.samuelsson@asphodel-meadows.net","true","21","Tiramisu"
"5","fe7164a1-3588-408d-bfa","a.wikstroem","Artemis","Wikstroem","Asphodel Meadows","a.wikstroem@asphodel-meadows.it","true","39","Nutella"
"6","b62601a3-80b4-48f5-853","j.magnusson","Juno","Magnusson","Lyonesse","j.magnusson@lyonesse.me","true","23","Sanguinaccio Dolce"
"7","41a90b61-c9f7-4192-aa2","s.joensson","Saturn","Joensson","Camelot","s.joensson@camelot.it","false","10","Panna Cotta"
"8","e47d4979-bb18-418b-834","d.wallin","Dionysus","Wallin","Ram Setu","d.wallin@ram-setu.com","true","94","Pasticciotto"
"9","6a59e76f-194d-4760-9b3","h.johansson","Hephaestus","Johansson","Asphodel Meadows","h.johansson@asphodel-meadows.net","","6","Panna Cotta"
"10","2f85f73f-39ab-4dc5-85f","s.lundqvist","Sol","Lundqvist","Meropis","s.lundqvist@meropis.com","false","69","Pasticciotto"
```

# Data Source JSON File
A data source that allows to load JSON data from a file in a java package:

```java
PFRDataSource DATA = 
		PFR.Data.newSourceJson("mainTestdataJSON", ENV.getTestdataPackage(), "testdata.json")
					.accessMode(AccessMode.SEQUENTIAL)
					.retainMode(RetainMode.ONCE)
					.build();
					;
```

The JSON parser used is set to lenient and tries to do whatever it cans to load the file.
The file has to contain a single array with JSON objects.
The fields of the JSON object can contain any type, including objects and arrays.

Example data structure:

```java
[
  {
    "INDEX": 0,
    "ID": "aca90d7d-e59e-4c91-b17",
    "USER": "u.bjoerk",
    "FIRSTNAME": "Uranus",
    "LASTNAME": "Bjoerk",
    "LOCATION": "Aztlan",
    "EMAIL": "u.bjoerk@aztlan.com",
    "LIKES_TIRAMISU": false,
    "VALUE": 66,
    "SEARCH_FOR": "Gianduiotto"
  },
  {
    "INDEX": 1,
    "ID": "459d167e-b905-44ec-a85",
    "USER": "r.viklund",
    ...
  }
  ...
]
```

# Data Source JSON Array
A data source that allows to load data from a JSON array containing JSON objects.
This can be useful to load data from a Web API, as in the following example:

```java
//=======================================
// Request Data
PFRHttpResponse r = PFRHttp.create("LoadTypicodeData", "https://jsonplaceholder.typicode.com/users") 
		.checkBodyContains("\"username\":")
		.send()
		.throwOnFail()
		;

//=======================================
// Create Source
JsonArray userArray = r.getBodyAsJsonArray();

PFRDataSource userData = PFR.Data.newSourceJsonArray("userList", userArray)
								.build();

PFRDataRecord record = userData.next();
System.out.println("========================");
System.err.println("id:"+record.get("id").getAsInteger());
System.err.println("username:"+record.get("username").getAsString());
System.err.println("address:"+ PFR.JSON.toJSON( record.get("address").getAsJsonObject()) );
System.err.println("All details:"+record.toString());
```


