package com.performetriks.performator.quickstart.tests.executors;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecCustom;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;

/***************************************************************************
 * This example a basic test with two standard scenarios
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestExampleCustom extends PFRTest {

	public PFRTestExampleCustom() {
		
		Globals.commonInitialization(true);
		
		this.add(new PFRExecCustom(UsecaseExampleHSR.class)
				.rampUp(20, 2, 2) 	// start 20 users, 2 users/interval, 5sec interval
				.stable(30)		  	// 20sec stable
				.rampDown(6, 1, 5)	// start 6 users, 1 users/interval, 5sec interval
				.stable(30)			// 20sec stable
				.start(10, 1000)	// start 10 users immediately with 1000 executions/hour
				.stable(30)			// 20sec stable
				.stop(5)			// stop 5 users immediately
				.stable(30)			// 20sec stable
				.killAll()			// killAll remaining users
		);

		this.maxDuration(Duration.ofSeconds(300));
		this.gracefulStop(Duration.ofSeconds(30));
		
	}

}
