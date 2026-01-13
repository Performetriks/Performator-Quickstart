
# Inserting Data 
You can use whatever methods or libraries you want to insert data and variables in your scripts.

# Pass as Parameters
Pass your variables as parameters to functions:

```java
PFRDataRecord record = Globals.DATA.next();
String user = record.getString("user");
String password = record.getString("password");

r = PFRHttp.create("010_Do_Login", url+"/app/login") 
					.POST()
					.param("username", user)
					.param("password", password)
```

# String Concatenation
Concatenate Strings:

```java
PFRDataRecord record = Globals.DATA.next();
String id = record.getString("id");

r = PFRHttp.create("010_GetOrders", url+"/api/orders?id="+id) 
					.get()
```

# Formatting Text-Blocks
To insert data into a java text-block, you can use the String.formatted()-method(works also with regular strings):

```java
PFRDataRecord record = Globals.DATA.next();
String searchTerm = record.getString("searchTerm");
String includeAll = record.getBoolean("includeAll");
int limit = record.getInteger("limit");

r = PFRHttp.create("010_Search", url+"/app/search") 
			.POST()
			.header("Content-Type", "application/json")
			.body("""
				{
					  "search": "%s"
					, "includeAll": %b
					, "limit": %d
				}
				""".formatted(searchTerm, includeAll, limit)
			)

```