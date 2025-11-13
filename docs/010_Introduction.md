# Performator-Quickstart Project
This is the Quickstart project of the Performator load testing framework!
This project contains lots of examples and is used as a base project that you can use to start your own projects.
It also contains all the documentation of all the plugins, so that everything is shipped and documented with your project and not somewhere in the depths of the internet.


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
