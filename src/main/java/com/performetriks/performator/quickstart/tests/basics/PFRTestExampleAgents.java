package com.performetriks.performator.quickstart.tests.basics;

import java.time.Duration;

import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRConfig;
import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.distribute.PFRAgent;
import com.performetriks.performator.distribute.PFRAgentPool;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleDataRead;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleSLA;

/***************************************************************************
 * This example a basic test with two standard scenarios
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestExampleAgents extends PFRTest {

	public PFRTestExampleAgents() {
		
		//#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!
		//#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!
		// !!! AGENTS ARE STILL CONSIDERED EXPERIMENTAL !!!
		//#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!
		//#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!#!
		Globals.commonInitialization(true);
		
		//-----------------------------
		// Set Data Shared
		Globals.DATA = PFR.Data.newSourceCSV("sharedData", Globals.ENV.getTestdataPackage(), "testdata.csv", ",")
				.shared()
				.build();
		;
		
		//-----------------------------
		// Set Agents
		PFRAgentPool pool = new PFRAgentPool(
				  new PFRAgent("deactivatedAgent", 1234	, "windows", "cloud").active(false)
				, new PFRAgent("winserver123", 1234	, "windows", "cloud")
				, new PFRAgent("localhost", 7777	, "windows", "dev", "test")
				, new PFRAgent("asusstrix", 7778	, "windows", "dev")
				, new PFRAgent("lenovop16s", 7778	, "windows", "test")
				, new PFRAgent("localhost", 7779	, "data")
			);
		
		PFRConfig.setAgentPool(pool);
		PFRConfig.setAgentTags("windows", "dev"); // filter agents that have all of these tags
		
		PFRConfig.setDataAgentPool(pool);	// can be same or separate pool
		PFRConfig.setDataAgentTags("data"); 

		//-----------------------------
		// Define Test

		this.add(new PFRExecStandard(UsecaseExampleDataRead.class, 10, 1000, 0, 1) );
		this.add(new PFRExecStandard(UsecaseExampleHSR.class, 60, 12000, 0, 5) );

		this.add(new PFRExecStandard(UsecaseExampleSLA.class)
						.users(6)
						.execsHour(2000)
						.rampUp(2) 
						.offset(20)
					);
		
		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(30));
		
	}

}
