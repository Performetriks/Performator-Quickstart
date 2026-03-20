# Performator-Quickstart
This is the Quickstart project of the Performator load testing framework!
This project contains lots of examples and is used as a base project that you can use to start your own projects.
It also contains all the documentation of all the plugins, so that everything is shipped and documented with your project and not somewhere in the depths of the internet.

The documentation of the Performator framework can be found in the folder "/docs" for the version the initiator of the project has cloned.
The most recent version can be found on GitHub: 
https://github.com/Performetriks/Performator-Quickstart/tree/main/docs

# Documentation
* [Introduction](./docs/010_Introduction.md)
* [Executors](./docs/050_Executors.md)
* [Data Sources](./docs/100_DataSources.md)
* [Data Extraction](./docs/150_DataExtraction.md)
* [Data Insertion](./docs/200_DataInsertion.md)
* [HSR Basics](./docs/250_HSR_Basics.md)
* [Plugin: Performator HTTP](./docs/300_PerformatorHTTP.md)
* [Agents](./docs/350_Agents.md)

# Copy & Paste Quick Access
Just a section with useful code snippets to copy & paste instead of having to search for them in the depths of the documentation:

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



