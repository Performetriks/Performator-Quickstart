# Executors
Executors define how a use case should be executed. They define the amount of load and such kind of things.

List of executors:

* [Executor Standard](#executor-standard): Executes a use case with a standard load pattern, based on amount of users and executions per hour.
* [Executor Once](#executor-once): Executes a use case once, useful for debugging, checks or functional tests.
* [Executor Repeat](#executor-repeat): Lets you execute a usecase sequentially with one user for a certain amount of repetitions.
* [Executor Increase](#executor-increase): Execute a usecase with increasing amount of users until a max amount of users is reached.
* [Executor Custom](#executor-custom): This executor let's you define and execute custom load patterns.
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

Executor Pattern:

```text
> = offset
# = execution time 
- = waiting time
| = pacing

User 1: >>>>|###-----------------|########------------|#-------------------|#####---------------|
User 2: >>>>    |#####---------------|###-----------------|#######-------------|#-------------------|
User 3: >>>>        |##------------------|##########----------|#####---------------|#####---------------|
User 4: >>>>            |##########----------|#####---------------|##------------------|######--------------|
User 5: >>>>                |####----------------|#-------------------|#####---------------|###-----------------|

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

Executor Pattern:

```text
> = offset
# = execution time 

User 1: >>>>###

```

# Executor Repeat
This executor lets you execute a usecase sequentially with one user for a certain amount of repetitions.
This can be useful to check all the records of your test data, for example:

```java
int recordCount = Globals.DATA.size();
this.add( new PFRExecRepeat(UsecaseCheckTestdata.class, recordCount) ); 
```

Executor Pattern:

```text
> = offset
# = execution time 
| = repetition

User 1: >>>>###|##|###|########|###

```
# Executor Increase
This executor lets you execute a usecase with increasing amount of users until a max amount of users is reached.
This is similar to the Standard executor, but instead of users and execs/hour, with this executor you control
the load by defining the rampUp and pacing.

This executor can be useful to do scalability testing.

```java
											//users	, Interval	, maxUsers	, pacing	, offset
new PFRExecIncrease(UsecaseExampleHSR.class, 1		, 3		, 1000		, 60		, 0);
```

Executor Pattern:

```text
> = offset
o = interval
# = execution time 
- = waiting time
| = pacing

User 1:   >>>>|###-----------------|########------------|#-------------------|#####---------------|
User 2:   >>>>oooo|#####---------------|###-----------------|#######-------------|#-------------------|
User 3:   >>>>    oooo|##------------------|##########----------|#####---------------|#####---------------|
User 4:   >>>>        oooo|##########----------|#####---------------|##------------------|######--------------|
User 5:   >>>>            oooo|####----------------|#-------------------|#####---------------|###-----------------|
[... a few moments later ...]
User N-1: >>>>                                          oooo|####----------------|#-------------------|#####---------------|###-----------------|
User N:   >>>>                                              oooo|####----------------|#-------------------|#####---------------|###-----------------|
```

# Executor Custom
This executor lets you create a custom load pattern by adjusting the number of users executed over time
This is useful in cases where you need to test very specific need that you can't fulfill with other executors.

The executer comes with various methods to define your load using a Builder pattern:

```java
new PFRExecCustom(Usecase.class)
    .rampUp(numUsers, userPerInterval, rampUpInterval)      // no pacing, rapidfire
    .rampUpPaced(numUsers, userPerInterval, pacingSeconds)  // calculate ramp up interval internally
    .rampUpExec(numUsers, userPerInterval, execHours)       // calculate pacing and ramp up intevall internally
    .start(numUsers)             // starts numUsers at same time, no pacing
    .start(numUsers, execHours)  // starts numUsers at same time, calculate pacing internally
    .stable(long millis)         // keep stable for defined time
    .stable(Duration duration)
    .rampDownGracefully(numUsers, userPerInterval, gracefulSeconds) // stop amount of users gracefully and gradually
    .rampDown(numUsers, userPerInterval)          // stop amount of users gradually and immediately
    .stopGracefully(numUsers, long gracefulTime)  // stop amount of users all at once but gracefully
    .stop(numUsers)          // stop amount of users immediately
    .killAll()               // kill all users that are currently running
    .offset(offsetSeconds)   // offset from the test start
    .percent(50)             // recalculate the load to the defined percent (must be at the end of the builder chain)

```

Following is an example of how to create a custom load pattern:

```java
this.add(new PFRExecCustom(UsecaseExample.class)
    .rampUp(10, 2, 3)   // start 10 users, 2 users/interval, 3sec interval
    .stable(10)         // 10sec stable
    .rampDown(6, 1, 3)  // ramp down 6 users, 1 users/interval, 3sec interval
    .stable(10)         // 10sec stable
    .start(5, 1000)     // start 10 users immediately with 1000 executions/hour
    .stable(10)         // 10sec stable
    .stop(3)            // stop 3 users immediately
    .stable(10)         // 10sec stable
    .killAll()          // killAll remaining users
);
```
		
**Executor Pattern:** Above custom executor definition would give you a change of active users over time as follows:

```text
* = user count

    .rampUp()       .stable()    .rampDown()  .stable()      .stable()        .stable()
|----------------|--------------|-----------|-----------|  |-------------|  |-------------|
                  * * * * * * * *                     
                *                 *                       * * * * * * * * *
              *                     *                     *               *                 .killAll()
            *                         *                   *               *                |-|
          *                             *                 *               * * * * * * * * * *
        *                                 *               *              |-|                *         
      *                                     * * * * * * * *               .stop()           *
    *                                                    |-|                                *
  *                                                       .start()                          *
*                                                                                           *
---------------------------------------------- Time ----------------------------------------------->
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

Executor Pattern:

```text
[...] = Other Executor
#     = execution time 

Executor 1: [####PFRExecOnce####]
Executor 2:                      [###################PFRExecStandard#####################]
Executor 3:                                                                               [###PFRExecOnce###]
...
```
