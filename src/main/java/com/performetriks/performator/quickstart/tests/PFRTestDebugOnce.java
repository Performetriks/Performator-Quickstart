package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecOnce;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHTTP;

public class PFRTestDebugOnce extends PFRTest {

	public PFRTestDebugOnce() {
		
		Globals.commonInitialization();
		
		this.add( new PFRExecOnce(UsecaseExampleHTTP.class, 15) ); 
		
		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(90));
		
	}

}
