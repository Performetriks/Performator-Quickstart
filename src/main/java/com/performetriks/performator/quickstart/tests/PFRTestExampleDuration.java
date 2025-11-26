package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleSLA;

/***************************************************************************
 * This example shows you how to set max duration for a use case.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestExampleDuration extends PFRTest {

	public PFRTestExampleDuration() {
		
		Globals.commonInitialization(true);
		
		int percentage = 100;

		// use methods maxDuration() and gracefulStop() to set usecase specifics
		this.add(new PFRExecStandard(UsecaseExampleHSR.class, 50, 12000, 0, 5)
							.percent(percentage) 
							.maxDuration( Duration.ofSeconds(30) )
							.gracefulStop( Duration.ofSeconds(5) )
						);
		
		this.add(new PFRExecStandard(UsecaseExampleSLA.class, 50, 12000, 0, 5)
							.percent(percentage)
							.maxDuration( Duration.ofSeconds(30) )
							.gracefulStop( Duration.ofSeconds(5) )
						);
		
		// The max durations for the whole test
		this.maxDuration(Duration.ofSeconds(600));
		this.gracefulStop(Duration.ofSeconds(60));
		
	}

}
