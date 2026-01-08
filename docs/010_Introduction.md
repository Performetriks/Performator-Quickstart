# Introduction
This page gives you an introduction into this quickstart project and an actual "quickstart" on how to use the Performator framework.

# Known Limitations
Here a list of known limitation of this project:
* Needs JDK 17 or higher
* Performator is made to run a single Test-Class at once.  

# Troubleshooting
**Data is not reported:**
* HSR might not be enabled, enable it with `HSRConfig.enable(REPORT_INTERVAL_SECONDS);`.
* Firewall might not be open, check logs for errors
* Code that registers reporters might be skipped, check that it is executed.
* Make sure `HSRConfig.enable(REPORT_INTERVAL_SECONDS);` is called to start the reporting.

**JVM Arguments are not passed to program:**
* Make sure to also forward them in the pom.xml in the exec-maven-plugin section using `<projectProperties />`:
```
<systemProperties>
	<projectProperties />
</systemProperties>
```

**Reporter File on disk is not updated:**
* Make sure the file is not opened in a program that prevents the file from being written, check logs for errors.

**Performance impact in load testing machine too high:**
* Reduce the log levels to reduce amount of logs
* Reduce number of reporters
* Reduce reporting interval

# Running a Test
To execute a test, run maven as follows and provide your test class with the property "-Dpfr_test":

```
mvn clean verify -Dpfr_test=com.performetriks.performator.quickstart.tests.PFRTestExample
```

# Terminology
For you to understand what you are gonna read, here a list of definitions:
* **HSR:** Short for [HieraStatsReport](https://github.com/xresch/HieraStatsReport), the reporting framework used by Performator. Ased as a prefix for most of the classes of the framework 
* **PFR:** Short for Performator, used as a prefix for most of the classes of the framework.
* **Use Case:** A set of steps that make up a test case.
* **Executor:** A mechanism that takes exactly one use case and executes it with a defined amount of load.
* **Test:** One or multiple combinations of (Executor + Usecase) and the configuration of the test itself, like test duration.


# Writing and Running Tests
Example code that uses Performator can be found in this repository under `src/main/java/` in the package `com.performetriks.performator.quickstart`.

The following classes are needed to implement a load test with Performator:
* **com.performetriks.performator.base.PFRUsecase:** Is extended to define the actual steps of a test case. Usecases are added to tests and are then run in parallel to create an actual simulation of concurrent user actions.
* **com.performetriks.performator.base.PFRExec*:** Classes that extend PFRExec, like PFRExecStandard, are used to define the load patterns of your PFRUsecases. There are various default classes that help you create your load pattern.
* **com.performetriks.performator.base.PFRTest:** Is extended and in the constructor the configuration for the test is setup, and the use cases are added with load executors.


**Creating a PFRUsecase:** To implement a use case, create a class that extends `PFRUsecase.java`.
The following code does a minimal implementation. As you see we are using the [HieraStatsReport(HSR)](https://github.com/xresch/HieraStatsReport) framework for our reporting, and we just simulate one step named "000_Open_Homepage":

``` java
public class UsecaseExampleMinimal extends PFRUsecase {

	@Override
	public void initializeUser() {}

	@Override
	public void execute() throws Throwable {

		HSR.start("000_Open_Homepage");
			Thread.sleep(HSR.Random.integer(50, 200));
		HSR.end();
				
	@Override
	public void terminate() {}

}
```

**Creating a PFRTest:** to implement a test, you create a class that extends `PFRTest` and add your use cases in the constructor by wrapping them with an implementation of `PFRExec`.
Following code is a simple demonstration of how to implement a test :

``` java
public class PFRTestExample extends PFRTest {

	public PFRTestExample() {
		
		// a simple way to keep initialization code in one place for multiple tests
		Globals.commonInitialization(); 
		
		int percentage = 100; // easy way to adjust load, e.g to 50% or 200%
		
		this.maxDuration(Duration.ofSeconds(90)); 	// the duration of the test
		// after maxDuration has been reached, add this grace period for users to finish their current execution
		this.gracefulStop(Duration.ofSeconds(90)); 	
		
		// Add our Usecase with a Standard Load Executor
		this.add(
			new PFRExecStandard(
					  UsecaseExampleMinimal.class
					, 50		// number of users
					, 12000	// use case executions per hour
					, 0			// offest from start
					, 5)		// ramp up for users
					.percent(percentage) // adapt percentage for above values
		);
	}
}

```

# Configuration

Configurations are done through *Config-classes, like:
  * `PFRConfig` for the configuration of Performator
  * `HSRConfig` for the configuration of the statistics engine (HieraStatsReport)
  * Plugin config like `PFRHttpConfig`.

## Scopes
Configurations can have three different scopes:
  * **Global:** Is set globally for any thread accessing HSR.
  * **Propagated:** Is set for the current thread and every thread that is spawned by that thread.
  * **Thread:** Is set for the current thread only, is not propagated .

The scope of a method is documented in it's javadoc.
To work with the **Propagated** scope, it is useful to know that it depends where you change a configuration:
  * **On Test: ** When you change the config in a Test-Class, it will affect all use cases and all users.
  * **On Usecase: ** When you change the config in a Usecase-Class, it will affect the user threads of that use case.
  

## Logging 
Performator is using SLF4J with Logback to manage the logging. 

When you want to create your own logger, Use the SLF4J `LoggerFactory` and cast the result to `ch.qos.logback.classic.Logger`:

``` java 
private static Logger logger = (Logger)LoggerFactory.getLogger(UsecaseExampleMinimal.class.getName());
```

The [HSR](https://github.com/xresch/HieraStatsReport) framework also provides methods to configure the LogLevels directly in code:

``` java
//--------------------------
// Set Log Levels
HSRConfig.setLogLevelRoot(Level.WARN);
HSRConfig.setLogLevel(Level.INFO, "com.performetriks.performator");
HSRConfig.setLogLevel(Level.INFO, "com.xresch.hsr");
```

Some of the Performator plugins provide additional methods to define logging, like the PFRHttp plugin:

``` java
//-------------------------------
// Configuration
PFRHttp.debugLogFail(true);	// log details for requests that fail
PFRHttp.debugLogAll(true); 	// log details for all request 
// add custom details to logs, very useful to find failing test data
PFRContext.logDetailsAdd("user", user); 
PFRContext.logDetailsAdd("data", data); 
```

## Reporting
All the reporting is done by the [HSR](https://github.com/xresch/HieraStatsReport) framework.
You register reporters with HSR, and the rest is magic that is done for you in the background.
If you have the need, urge, mission or other causes to create your own reporter, you find examples on how to do so [here](https://github.com/xresch/HieraStatsReport/tree/main/src/main/java/com/xresch/hsr/reporting). 

``` java
//--------------------------
// Set Test Properties
HSRConfig.addProperty("[Custom] Environment", "TEST");
HSRConfig.addProperty("[Custom] Testdata Rows", "120");

//--------------------------
// Optional: Disabling System Usage Stats
HSRConfig.statsProcessMemory(false);
HSRConfig.statsHostMemory(false);
HSRConfig.statsCPU(false);
HSRConfig.statsDiskUsage(false);
HSRConfig.statsDiskIO();
HSRConfig.statsNetworkIO();

//--------------------------
// Optional: Log every datapoint
// potential performance impact, debug use only
HSRConfig.setRawDataLogPath(DIR_RESULTS+"/raw.log");

//--------------------------
// Define Sysout Reporters
HSRConfig.addReporter(new HSRReporterSysoutCSV(" | "));
//HSRConfig.addReporter(new HSRReporterSysoutJson());

//--------------------------
// Define File Reporters
HSRConfig.addReporter(new HSRReporterJson( DIR_RESULTS + "/hsr-stats.json", true) );
HSRConfig.addReporter(new HSRReporterCSV( DIR_RESULTS + "/hsr-stats.csv", ",") );
HSRConfig.addReporter(new HSRReporterHTML( DIR_RESULTS + "/HTMLReport") );

//--------------------------
// Database Reporters
HSRConfig.addReporter(
	new HSRReporterDatabasePostGres(
		"localhost"
		, 5432
		, "postgres"	// dbname
		, "hsr"			// table name prefix
		, "postgres"	// user
		, "postgres"	// pw
	)
);
		
//--------------------------
// Enable
HSRConfig.enable(REPORT_INTERVAL_SECONDS); 
```

# Jenkins Integration
For integrating Performator with Jenkins, creating a pipeline using a Jenkinsfile is recommended.
An example of a Jenkinsfile has been added to this project.

The example file does the following:
* **Parameters: ** Defines parameters that can be adapted to set the test.
* **Build & Run: ** Builds and runs the test using Maven. (Note: if you running jenkins on linux, you might need to change "bat" to "sh")
* **Archive Report:** Archiving the report.zip generated by Maven, creating a download link on the build status page.
* **Publish HTML Report:** Using the HTML Publisher Plugin to make the HTML Reports available in Jenkins. Creates a menu point "HTML Report" in the navigation side bar of the build.

## HTML Reports in Jenkins and Security
Viewing the published HTML report in Jenkins will only work when you change the Content-Security-Policy header of your Jenkins instance. 
This is done on the command line used to start the jenkins process (e.g. configurable in `<JENKINS_HOME>/jenkins.xml`)

The following parameters has to be added:

```
-Dhudson.model.DirectoryBrowserSupport.CSP="sandbox allow-same-origin allow-scripts allow-downloads; unsafe-inline;"
```


