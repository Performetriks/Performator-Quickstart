# Introduction
This page gives you an introduction into this quickstart project and an actual "quickstart" on how to use the Performator framework.

# Known Limitations
Here a list of known limitation of this project:
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
To execute a test, run maven as follows:
```
mvn clean verify -Dpfr_test=com.performetriks.performator.quickstart.tests.PFRTestExample
```
