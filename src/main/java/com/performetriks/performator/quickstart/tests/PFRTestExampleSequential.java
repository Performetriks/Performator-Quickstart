package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecOnce;
import com.performetriks.performator.executors.PFRExecSequential;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleSLA;
import com.performetriks.performator.quickstart.usecase.UsecaseLoadDataCustom;
import com.performetriks.performator.quickstart.usecase.UsecaseLoadDataWeb;

/***************************************************************************
 * This example a basic test with two standard scenarios
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestExampleSequential extends PFRTest {

	public PFRTestExampleSequential() {
		
		Globals.commonInitialization(true);
		
		this.add(
			new PFRExecSequential()
				.add( new PFRExecOnce(UsecaseLoadDataCustom.class) )
				.add( new PFRExecStandard(UsecaseExampleHSR.class, 10, 5000, 0, 5).maxDuration(Duration.ofSeconds(30) ) )
				.add( new PFRExecOnce(UsecaseLoadDataWeb.class) )
				
		);

		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(30));
		
	}

}
