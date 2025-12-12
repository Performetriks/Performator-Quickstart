package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecIncrease;
import com.performetriks.performator.executors.PFRExecOnce;
import com.performetriks.performator.executors.PFRExecSequential;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleSLA;
import com.performetriks.performator.quickstart.usecase.UsecaseLoadDataCustom;
import com.performetriks.performator.quickstart.usecase.UsecaseLoadDataWeb;

/***************************************************************************
 * This example shows how to run a test with increasing load up to a maximum
 * amount of users.
 * This is similar to PFRExecStandard, however with this executor you control
 * the load by defining based on the rampUp and the pacing.
 * 
 * This is useful to do scalability testing and find breaking points in your application.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestExampleIncrease extends PFRTest {

	public PFRTestExampleIncrease() {
		
		Globals.commonInitialization(true);
															//users	, Interval	, maxUsers	, pacing	, offset
		this.add( new PFRExecIncrease(UsecaseExampleHSR.class, 1	, 3			, 1000		, 60		, 0) );
		
		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(30));
		
	}

}
