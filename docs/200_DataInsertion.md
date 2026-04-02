
# Inserting Data 
You can use whatever methods or libraries you want to insert data and variables in your scripts.

# Pass as Parameters
Pass your variables as parameters to functions:

```java
XRRecord record = Globals.DATA.next();
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
XRRecord record = Globals.DATA.next();
String id = record.getString("id");

r = PFRHttp.create("010_GetOrders", url+"/api/orders?id="+id) 
					.get()
```

# Text-Blocks: XRRecord.insert()
Data records provide you with an easy method to replace your parameters in strings with the `insert()`-method.
You can define parameters using the syntax `${fieldname}` and whatever value is assigned to "fieldname" in your data record will be inserted
in your string.

```java
// data contains fields "searchTerm", "includeAll" and "limit"
XRRecord data = Globals.DATA.next();
data.add("customValue", "Forty-Two gallons of Tiramisu.")

r = PFRHttp.create("010_Search", url+"/app/search") 
			.POST()
			.header("Content-Type", "application/json")
			.body(
				data.insert("""
					{
						  "search": "${searchTerm}"
						, "includeAll": ${includeAll}
						, "limit": ${limit}
						, "custom": "${customValue}"
					}
					""")
			)

```

# Text-Blocks: String Formatting
To insert data into a java text-block, you can use the String.formatted()-method(works also with regular strings):

```java
XRRecord record = Globals.DATA.next();
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

# Replace Custom Placeholders
Another way is to define costom placeholders, and use String.replace() to replace them.
This can be useful if you have lots of parameters that you have to replace.

```java
XRRecord record = Globals.DATA.next();
String searchTerm = record.getString("searchTerm");
String includeAll = record.getBoolean("includeAll");
String limit = record.getString("limit");

r = PFRHttp.create("010_Search", url+"/app/search") 
			.POST()
			.header("Content-Type", "application/json")
			.body("""
				{
					  "search": "${searchTerm}"
					, "includeAll": ${includeAll}
					, "limit": ${limit}
				}
				""".replace("${searchTerm}", searchTerm)
        			.replace("${includeAll}", includeAll)
        			.replace("${limit}", limit)
        			;
			)

```