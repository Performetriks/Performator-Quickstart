# Performator Database
Following is the documentation of the Performator HTTP plugin.
 

# Troubleshooting

### Error: Too many clients already:
You might get the following error message:

```
Database Error: FATAL: sorry, too many clients already
```

This means you are not closing all the query connections. Solution:

```java
//------------------------------
// Query and get Result Set
ResultSet r = db.create().query("SELECT * from table").toResultSet();
// do something with the result set, then close
db.close(r);

//------------------------------
// Close after you query
db.create().query(""SELECT * from table"")
	.close();
```

# Basics

### Maven Dependencies
There is no additional dependency, the `PFRDB` feature is coming from the core dependency:

```java
<!-- https://mvnrepository.com/artifact/com.performetriks/performator -->
<dependency>
    <groupId>com.performetriks</groupId>
    <artifactId>performator</artifactId>
    <version>#.#.#</version>
</dependency>
);
```

However, you will need to define the dependencies that contain the JDBS driver for your database, e.g, PostGres:

```java
	<!-- https://mvnrepository.com/artifact/org.postgresql/postgresql -->
	<dependency>
		<groupId>org.postgresql</groupId>
		<artifactId>postgresql</artifactId>
		<version>#.#.#</version>
	</dependency>
```

### Classes
Following are the most important classes of the DB testing:
* **PFRDB:** The main class that allows you to create a DB interface for testing.
* **PFRDBSQLBuilder:** The class that is configuring the DB calls and measures the times.



# Connecting to a Database

**Note**: Make sure you have included the dependency that contains your JDBC driver.
To connect to a database, use one of the `PFRDB.init*()` methods. It will return an interface that you can use to create your DB calls.
Here is an example how to connect to a PostGres Database:

```java
	private static PFRDB db = PFRDB.initDBInterfacePostgres(
			  "localhost"
			, 5432
			, "postgres"	// dbname
			, "postgres"	// user
			, "postgres"	// pw
		);
```

# Database Call Examples

###  Query
Simple query call.

```java
db.create() // no metricName = will use SQL as name
	.query( "SELECT * from table")
	.close();
```

###  Query with Placeholders
Query call with a prepared statement and placeholders.

```java
int id = 1;
db.create() // no metricName = will use SQL as name
	.query( "SELECT * FROM table WHERE id = ?", id)
	.close();
```

###  Query: Custom Metric Name
Useful when your queries are bigger.

```java
db.create("My Metric Name")
	.query( "SELECT * from table")
	.close();
```

###  Query: With SLA
You can add an SLA using one of the `.sla()` methods on the PFRSQLBuilder.

```java

private static final HSRSLA SLA_DEFAULT = 
		new HSRSLA(HSRMetric.p90, Operator.LTE, 200) // p90 <= 500ms
			  .and(HSRMetric.failrate, Operator.LTE, 5); // failure rate <= 5%
				  
db.create("My Metric Name")
	.sla(HSRMetric.p90, Operator.LTE, 100)
	.sla(SLA_DEFAULT)
	.query( "SELECT * from table")
	.close();
```

###  Query: With Ranged Metric
You can enable ranged metrics for the amount of rows in the result
using the method `.enableRangedMetrics()`:

```java
//------------------------------
// Query with Ranged Metrics
db.create()
	.enableRangedMetrics("#Records", 100)
	.query("SELECT * from table")
	;
```


###  Query: Get ResultSet
There are many `to*()` methods that you can use to convert your results.
Here is an example of how to get the result set:

```java
ResultSet r = db.create() // no metricName = will use SQL as name
		.query( "SELECT * from table")
		.toResultSet()
		;

// do something with the result set, then close
db.close(r);
```


###  Insert
Use the `.execute()` method to make an insert statement.
Alternatively there is a method `.insertGetKey()` that also retrieves the generated primary key.

```java
db.create("My Insert")				
	.execute("""
		INSERT INTO 
			(time, name, description, properties, tags)
			VALUES (?,?,?,?,?)
		"""
		, System.currentTimeMillis()
		, "Name"
		, "Description"
		, "{my properties}"
		, "tag,1,2,3,4")
	)
	;
```

###  Update and Batch
For update use the `.update()` method and for batch use  the `.batch()` method.




