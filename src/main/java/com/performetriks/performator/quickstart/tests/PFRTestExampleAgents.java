package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRConfig;
import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.distribute.PFRAgent;
import com.performetriks.performator.distribute.PFRAgentPool;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
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
		
		Globals.commonInitialization(true);
		
		//-----------------------------
		// Set Agents
		PFRAgentPool pool = new PFRAgentPool(
				  new PFRAgent("localhost", 7777)
				, new PFRAgent("localhost", 7778)
				//, new PFRAgent("localhost", 7779)
			);
		
		PFRConfig.setAgentPool(pool);

		//-----------------------------
		// Define Test
		int percentage = 100;

		this.add(new PFRExecStandard(UsecaseExampleHSR.class, 60, 12000, 0, 5).percent(percentage) );

		this.add(new PFRExecStandard(UsecaseExampleSLA.class)
						.users(6)
						.execsHour(2000)
						.rampUp(2) 
						.offset(20)
						.percent(percentage)
					);
		
		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(30));
		
	}

}
