# Executors
Executors define how a use case should be executed. They define the amount of load and such kind of things.

List of executors:

* [Executor Standard](#executor-standard): Executes a use case with a standard load pattern, based on amount of users and executions per hour.
* [Executor Once](#executor-once): A simple executor that executes a use case once, useful for debugging, checks or functional tests.
* [Executor Sequential](#executor-sequential): This executor let's you execute other executors in sequence.

# Executor Standard
Executes a use case with a standard load pattern, based on amount of users and executions per hour.
The users will be ramped up over time until the full target user amount is reached. Then the 
users will stay stable until the end of the test.

```java
this.add(
	new PFRExecStandard(
					  UsecaseExample.class
					, 7		// target concurrent users
					, 1400	// executions per hour
					, 0		// offset from test start in seconds
					, 5		// number of users to add per ramp up interval
					);
);
```

You can also use the percent()-function to calculate a percentage of the load you have defined.
Useful to test the same scenario with half the load or twice the load and so on. 

```java
this.add(
	new PFRExecStandard(UsecaseExampleHTTP.class, 7, 1400, 0, 5)
			.percent(50) // mave 50% 
);
```

# Executor Once
A simple executor that executes a use case once, useful for debugging, checks or functional tests.

```java
this.add( new PFRExecOnce(UsecaseExample.class) );
```

You can as well specify an offset in seconds for starting it later.

```java
// wait for 30 seconds before execution
this.add( new PFRExecOnce(UsecaseExampleHTTP.class, 30) ); 
```

# Executor Sequential
This executor let's you execute other executors in sequence.

```java
this.add(
	new PFRExecSequential()
		.add( new PFRExecOnce(UsecaseFirst.class) )
		.add( new PFRExecStandard(UsecaseSecond.class, 10, 5000, 0, 5).maxDuration(Duration.ofSeconds(30) ) )
		.add( new PFRExecOnce(UsecaseLast.class) )
		
);
```
