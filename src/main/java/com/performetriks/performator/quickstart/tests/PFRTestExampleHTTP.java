package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRContext;
import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHTTP;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleSLA;

public class PFRTestExampleHTTP extends PFRTest {

	public PFRTestExampleHTTP() {
		
		Globals.commonInitialization(true);
		
		int percentage = 100;

		this.add(new PFRExecStandard(UsecaseExampleHTTP.class, 7, 1400, 0, 5).percent(percentage) );

		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(90));
		
	}

}
