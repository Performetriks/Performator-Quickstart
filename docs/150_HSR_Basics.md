# HSR Basics
This page only contains some basics for the HieraStatsReport(HSR) framework.
Please find more documentation on the official repository: [HieraStatsReport README](https://github.com/xresch/HieraStatsReport/blob/main/README.md)

# Logging

### Log Levels
The HSR framework is using Logback for logging. It provides methods to set log levels for packages in code.
Here is how you can configure logs:

```java
//--------------------------
// Log Levels
HSRConfig.setLogLevelRoot(Level.WARN);
HSRConfig.setLogLevel(Level.WARN, "com.xresch.hsr.reporting");

//----------------------------
// Change Log Interceptor
HSRConfig.setLogInterceptor(new HSRLogInterceptorDefault(Level.ERROR));
[...]
HSRConfig.setInterval(15);
HSRConfig.enable();

//--------------------------
// Optional: Log every datapoint
// potential performance impact
HSRConfig.setRawDataLogPath(DIR_RESULTS+"/raw.log");
```

# Properties
You can add global properties to your report. These will show up in some reports like the HTML report.

```java
//--------------------------
// Set Test Properties
HSRConfig.addProperty("[Custom] Environment", "TEST");
HSRConfig.addProperty("[Custom] Testdata Rows", "120");
```

# Turning off System Metrics
You can turn off specific system metrics using the methods `HSRConfig.stats*(boolean);`.

# Reporters
Reporters are the way of getting data to where you want it to be.
HSR ships with various default reporters, or you can create your own reporter class.
Reporters are registered with `HSRConfig.addReporter()` and should be done before calling `HSRConfig.enable()`.

```java
//--------------------------
// Define Sysout Reporters
HSRConfig.addReporter(new HSRReporterSysoutAsciiTable(75));
HSRConfig.addReporter(new HSRReporterSysoutCSV(" | "));
HSRConfig.addReporter(new HSRReporterSysoutJson());

//--------------------------
// Define File Reporters
HSRConfig.addReporter(new HSRReporterJson( DIR_RESULTS + "/hsr-stats.json", true) );
HSRConfig.addReporter(new HSRReporterCSV( DIR_RESULTS + "/hsr-stats.csv", ",") );
HSRConfig.addReporter(new HSRReporterHTML( DIR_RESULTS + "/HTMLReport") );

//--------------------------
// Database Reporter
HSRConfig.addReporter(
	new HSRReporterDatabasePostGres(
		"localhost"
		, 5432
		, "postgres"	// dbname
		, "hsr"		// table name prefix
		, "postgres"	// user
		, "postgres"	// pw
	)
);
```


# Custom Reporters
You can create your custom reporter by implementing the interface HSRReporter.
Feel free to copy and just one of the existing implementations.
* Interface: com.xresch.hsr.reporting.HSRReporter
* Example Implementations: https://github.com/xresch/HieraStatsReport/tree/main/src/main/java/com/xresch/hsr/reporting

# Reporting Metrics
Metrics can be reported as different types that are defined by `com.xresch.hsr.stats.HSRRecord.HSRRecordType`.
For most of them, the HSR-Class provides methods to create the metrics:

```java
Step, Group, User, Metric, Count, Gauge, System, Assert, Wait, Exception, MessageInfo, MessageWarn, MessageError, Unknown
```

Here some examples on how to create metrics:

```java
//-------------------------------
// Step
HSR.start("010_MyMetricName");
	//Code to measure
HSR.end();

//-------------------------------
// Group
HSR.startGroup("017_MyGroup");
	HSR.start("020_Execute_Search");
		//Code to measure
	HSR.end();
	
	// [...] other steps
HSR.end();

//-------------------------------
// Add Messages
HSR.addInfoMessage("The train will leave at 4:16 PM.");
HSR.addWarnMessage("The train is delayed");
HSR.addErrorMessage("The train has been cancelled.");

//-------------------------------
// Add Exception
HSR.addException(new Exception("This is an exception."));

//-------------------------------
// Assertions
HSR.assertEquals("A"
		, HSR.Random.fromArray(new String[] {"A", "A", "A", "B"})
		,  "060_Assert_ContainsA");

//-------------------------------
// Custom Assertions
boolean isSuccess = ( "expected".equals("actual") );
HSR.addAssert("MyAssertMessage", isSuccess);

//-------------------------------
// Gauge
// will be averaged in aggregation
HSR.addGauge("070.1 Gauge: SessionCount", HSR.Random.bigDecimal(80, 250));

//-------------------------------
// Count
// will be summed up in aggregation 
HSR.addCount("070.2 Count: TiramisusEaten", HSR.Random.bigDecimal(0, 100));
HSR.addInfoMessage(HSR.Random.from("Valeria", "Roberta", "Ariella") + " has eaten the Tiramisu!");

//-------------------------------
// Metric
// will calculate statistical values 
HSR.addMetric("070.3 Metric: TimeWalked", HSR.Random.bigDecimal(100, 300));						

//-------------------------------
// Ranged Metric
// used to analyze correlation between count and duration
int multiplier = HSR.Random.integer(0, 10);
int count = multiplier * HSR.Random.integer(1, 900);
int duration = multiplier * HSR.Random.integer(10, 1000);
HSR.addMetricRanged("070.4 TableLoadTime", new BigDecimal(duration), count, 50);					
```

# Service Level Agreements (SLA)
How to define SLAs and register them with the start()-method.

### SLA (P90 <= 100ms) and (AVG <= 50ms)
You can use the and-method to combine as many criteria as you want.

```java
private static final HSRSLA SLA_P90_AND_AVG = 
			new HSRSLA(HSRMetric.p90, Operator.LTE, 100)
				  .and(HSRMetric.avg, Operator.LTE, 50); 
	
HSR.start("110_SLA_P90_AND_AVG-OK", SLA_P90_AND_AVG);
	Thread.sleep(HSR.Random.fromInts(5, 10, 20, 30, 40, 90));
HSR.end();
```

### SLA (failurerate <= 10%)
Following an example on how to check the failure rate.

```java
private static final HSRSLA SLA_FAILRATE_LT_10 = 
		new HSRSLA(HSRMetric.failrate, Operator.LT, 10); 
		
HSR.start("150_SLA_FAILS_LT_10-OK", SLA_FAILRATE_LT_10);
	Thread.sleep(HSR.Random.fromInts(60, 60, 60, 60, 60, 110));
HSR.end( (HSR.Random.integer(0, 100) > 5) ? true : false );

```

