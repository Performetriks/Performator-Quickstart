
# Extracting Data 
You can use whatever methods or libraries you want to extract information from the results you obtain.
Here are some examples using what comes built-in with Performator.

# Extract from String

## Extract Bounds
Following example shows you how to extract a list of IDs from a JSON response using `PFR.Text.extractBounds()`:

```java
ArrayList<String> ids = PFR.Text.extractBounds("\"PK_ID\":", ",", responseBody);
logger.info("List of IDs: "+ String.join(", ", ids));
```

## Extract Regex Group
Following example shows you how to extract a list of Names from a JSON response using `PFR.Text.extractRegexAll()`:
The regex functions provided by `PFR.Text.*` will also cache the compiled regular expression to decrease performance overhead.

```java
ArrayList<String> names = PFR.Text.extractRegexAll("\"NAME\":\"(.*?)\",", 0, responseBody);
logger.info("List of Names: "+ String.join(", ", names));
```

## Get JsonArray
Here is how you can get a json-string as a JsonObject and retrieve its "payload" as a JsonArray:

```java
JsonArray dashboardArray = PFR.JSON.fromJson(responseBody).getAsJsonArray();
//logger.info("List of Dashboards: "+ PFR.JSON.toJSONPretty(dashboardArray));
```

# Extract from Json
To extract data from Json responses, we recommend using the Jayway JsonPath library.
The library is very powerful, you can find it's documentation on GitHub: https://github.com/json-path/JsonPath 

Maven Dependency:

```xml
<!-- https://mvnrepository.com/artifact/com.jayway.jsonpath/json-path -->
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>#.#.#</version>
</dependency>
```

As JsonPath parses the response, it is recommended to parse it once in case you do multiple extractions to reduce performance impact:

```java
DocumentContext ctx = JsonPath.parse(r.getBody());
```

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

