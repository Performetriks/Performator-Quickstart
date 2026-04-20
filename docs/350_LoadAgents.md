
# Load Test Agents
(**Note:** This feature is considered to be in experimental stage.)
AGents can be used to distribute your test onto multiple machines. This can be useful if you need to execute tests that need a lot of machine power, either because of high amount of transactions or because of the technology you execute(e.g. broswer simulations).

Compiled agent binaries can be found in the releases of the [Performator Repository](https://github.com/Performetriks/Performator/releases).

# Terms
* **Agent:** A process that receives a compiled test jar file and executes it.
* **Agent Machine:** A machine an agent is running on.
* **Agentborne:** The processes that are spawned by the agent. These are executing the test, not the agent itself. 
* **Coordinator:** The test process that starts the test, distributes it to the agents, collects statistics from agents and does the reporting.
* **Coordinator Machine:** The machine on which you start the test on.

# Troubleshooting

### Agents unavailable after Interrupting Test
Wait 1 minute before starting your test again. The agents are still reserved but should self heal after about 1 minute. 

How self-healing works: 

```shell
1. Coordinator reserves an agent and uploads the compiled test jar-file(basically itself).
2. While executing a test, Coordinator pings the agent every 15 seconds(not the same as reporting interval).
3. When you interrupt the test there won't be any more pings.
4. When the Agent does not receive a ping for 1 minute, it will stop the Agentborne process and make itself available again.
```

### Can't Transfer .jar-File
You might get an error like the following:

```shell
09:08:18.967 [main] ERROR com.performetriks.performator.distribute.ZePFRClient -- Issue while loading and transferring jar-file to remote agent.
java.nio.file.AccessDeniedException: Z:\Repositories\Performator-Test\target\classes
```

This is in most cases caused when executing the test with Maven and the exec-maven-plugin is not correctly configured. Please check the original pom.xml to find the correct setup.

### Test aborted, but I need the Data and Logs!

Check the execution directory of your agents. The agent will retain the data for the last 10 test executions in sub directories.
You should be able to find:
* **{AGENT_HOME}/target/performator-*.log: *** The log file of the Agent, containing the logs of the Agent itself.
* **{AGENT_HOME}/{TESTFOLDER}/target/performator-*.log: *** The log file of the Agentborne process, contains the log output of your test execution.
* **{AGENT_HOME}/{TESTFOLDER}/target/report/*: *** The basic statistics files that were written during the test: CSV, JSON, and if the test completed a HTML Report.

### System Metrics Graphs Look Strange
You are using the same machine to execute your Coordinator and an Agent process, or you might run two agents on the same machine. 

### Percentile Values Equals Max
Increase your test duration or your reporting interval (e.g. 60 sec instead of 15 sec). Percentiles are only useful when you have a good amount of data. Distributing the load to many agents will reduce the amount of values used to calculate the percentile values. The more data you have per agent and interval the more accurate your test will be. 

Aggregation stages: 

```shell
1. Agentborne procees aggregates the raw metrics to statistics per reporting interval.
2. Coordinator aggregates statistics retrieved from all Agentbornes(via Agent) per reporting interval.
3. At the end of the test, Coordinator summarizes all the statistical datapoints collected over time to a final summary statistic.
```


# Setup an Agent

Prerequisites:
* **Java Version:** Java 17 or higher.
* **Permissions:** The user running the process needs read/write/execute permissions in the execution directory.
* **Ports:** The agent port(Default 9876) needs to be open between the Agent machine and the Coordinator machine.

Not Prerequisites:
* **Opening Ports to Reporting Targets:** Reporting is done from your Coordinator machine, not from agents. All registered reporters are removed on the Agentborne process and replaced with a default CSV, JSON and HTML Reporter.

Following an example command that starts the agent. Parameters:
* **-Dpfr_mode=agent:** Tells the binary to start as an agent.
* **-Dpfr_port:** The port of the agent, default is 9876.
* **-Dpfr_agentbornePort:** The port of the test processes that are spawned by the agent, default is 9877.
* **-Dpfr_loglevel:** The root log level of the agent(doesn't affect agentborne). Options: 'INFO', 'ERROR', 'WARN', 'INFO', 'DEBUG', 'TRACE', 'ALL', 'OFF'


```shell
java -Dpfr_mode=agent -Dpfr_port=9876 -Dpfr_agentbornePort=9877 -Dpfr_loglevel=INFO -jar performator-agent-#.#.#.jar
```

# Define Agents
Create a PFRAgentPool and define your agents using PFRAgent.
You can add tags to your agents and deactivate whenever required using `.active(false)`.

```java
//-----------------------------
// Set Agents
PFRAgentPool pool = new PFRAgentPool(
		  new PFRAgent("deactivatedAgent", 9876	, "windows", "cloud").active(false)
		, new PFRAgent("winserver123"    , 9876	, "windows", "cloud")
		, new PFRAgent("localhost"       , 9876	, "windows", "dev", "test")
		, new PFRAgent("asusstrix"       , 9876	, "windows", "dev")
		, new PFRAgent("lenovop16s"      , 9876	, "windows", "test")
	);

PFRConfig.setAgentPool(pool);
PFRConfig.setAgentTags("windows", "dev"); // filter agents that have all of these tags

```

# Executing a Test with or without Agents
You use the same command to execute a test with agents as you do execute a regular test. This executes the test with the default `-Dpfr_mode=auto`, which detects if you have defined agents for your test or not.

```java
mvn clean verify -Dpfr_test=com.performetriks.performator.quickstart.tests.basics.PFRTestExampleAgents
```

If you want to force your test to execute on localhost without using agents, you can specify the parameter `-Dpfr_mode=local`, which will ignore any agent definitions and runs the full test locally.

```java
mvn clean verify -Dpfr_mode=local -Dpfr_test=com.performetriks.performator.quickstart.tests.basics.PFRTestExampleAgents
```

# Data Sharing between Agents
When load testing business applications, you often run into the issue that you shouldn't edit the same item, customer, record etc... at the same
time, as this might lead to inconsistent behavior. One common solution for this is to have a fairly large amount of test data records and shuffle the data on every agent. This will reduce the likelihood that a record is used at the same time by two agents but does not fully prevent it:

```java
//--------------------------
// Load Test Data CSV	
PFRDataSource DATA = 
		PFR.Data.newSourceCSV("com.mypackage.data", "testdata.csv", ",")
					.shuffle()
					.build();
;
);
```

Performator provides the possibility to define a Data Agent and set PFRDataSource.shared(). That data is then managed by the Data Agent instead of having every Load Agent managing their own data set. 

```java
//-----------------------------
// Set Data Shared
PFRDataSource DATA = PFR.Data.newSourceCSV("sharedData", Globals.ENV.getTestdataPackage(), "testdata.csv", ",")
		.shared()
		.build();
;
//-----------------------------
// Set Agents
PFRAgentPool pool = new PFRAgentPool(
		, new PFRAgent("localhost", 7777	, "windows", "dev", "test")
		, new PFRAgent("asusstrix", 7778	, "windows", "dev")
		, new PFRAgent("localhost", 7779	, "data")
	);

PFRConfig.setAgentPool(pool);
PFRConfig.setAgentTags("windows", "dev"); // filter agents that have all of these tags

PFRConfig.setDataAgentPool(pool);	// can be same or separate pool, will only pick a single agent, never multiple
PFRConfig.setDataAgentTags("data"); 

```

If you want to have a look at the data on the Data Agent, here are some useful API endpoints:

```shell
# retrieves the next record of the DataSource
http://data-agent-host:7779/api?command=datasourcenext&datasourceName=sharedData
```


# Web API Endpoints
There are a few web API endpoints that can be useful:

```shell
# returns the status of the agent
http://agenthost:9876/api?command=status

# returns process log of the agentborne process once and discards it, up to the last 10'000 lines
http://agenthost:9876/api?command=processlog

# peek the stats that are currently on the agentborne process, will be empty most of the time
http://agenthost:9876/api?command=statspeek

```
