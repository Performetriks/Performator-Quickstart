
# Extending Performator
Performator can be extended with your own custom code to address specific needs you might encounter working in a company or a project.

# Maven Dependencies

To extend performator, the project you create need to define the performator dependency:

```java
<!-- https://mvnrepository.com/artifact/com.performetriks/performator -->
<dependency>
    <groupId>com.performetriks</groupId>
    <artifactId>performator</artifactId>
    <version>#.#.#</version>
</dependency>
);
```

# Creating a Custom Data Source
To create a custom data source, you have to extend one of the following abstract classes:

```java
// The base abstract class for data sources
com.performetriks.performator.data.PFRDataSource
// For static data sources that load all data once when initialized
com.performetriks.performator.data.PFRDataSourceStatic
```

The easiest way to start is to copy one of the existing [Data sources](https://github.com/Performetriks/Performator/tree/main/src/main/java/com/performetriks/performator/data).

There is no need to register the new class to the framework. You just use it as is.

You can use your new class directly to manage whatever data you are loading:

```java
	PFRDataSource mySource = 
		new PFRDataSourceYours(...)
				.random()
				.build();
				
	XRRecord record = mySource.next();
```


# Creating a Custom Executor
To create a custom executor, you have to extend the following abstract class:

```java
com.performetriks.performator.executors.PFRExec
```

The easiest way to start is to copy one of the existing [Executors](https://github.com/Performetriks/Performator/tree/main/src/main/java/com/performetriks/performator/executors). Be aware that the executor PFRExecSequential is executing other executors and not a usecase.

You can use your new executer when you create a PFRTest, for example:

```java
public class PFRTestExample extends PFRTest {
	public PFRTestExample() {
		this.add(new PFRExecYours( ... );
	}
}
```

# Creating a Custom Report
To create a custom reporter, you have to extend the following abstract class:

```java
com.xresch.hsr.reporting.HSRReporter
```

The easiest way to start is to copy one of the existing [Reporters](https://github.com/xresch/HieraStatsReport/tree/main/src/main/java/com/xresch/hsr/reporting).

You can use your new reporter when like the existing ones by adding it to HSRConfig:

```java
HSRConfig.addReporter(new HSRReporterYours(...) );
```
