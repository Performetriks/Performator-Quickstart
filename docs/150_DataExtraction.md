
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

## List of IDs
Extracts from `{"payload": [{"id": 1}, {"id": 2}, ...]}`:

```java
List<Integer> allIDs = ctx.read("$.payload[*].id");
```

## Length of Array
Extracts from `{"payload": [{"id": 1}, {"id": 2}, ...]}`:

```java
Integer count = ctx.read("$.payload.length()");
```

## First ID in List
Extracts from `{"payload": [{"id": 1}, {"id": 2}, ...]}`:

```java
Integer firstName = ctx.read("$.payload[0].id");

```

## Last ID in List
Extracts from `{"payload": [{"id": 1}, {"id": 2}, ...]}`:

```java
Integer lastName = ctx.read("$.payload[-1].NAME");

```

## Extract Boolean
Extracts from `{"payload": [{"isActive": true}, {"isActive": false}, ...]}`:

```java
Boolean thirdIsActive = ctx.read("$.payload[0].isActive");

```

