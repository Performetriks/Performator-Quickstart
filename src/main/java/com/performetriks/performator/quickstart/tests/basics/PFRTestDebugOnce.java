package com.performetriks.performator.quickstart.tests.basics;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecOnce;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseLoadDataCustom;
import com.xresch.hsr.base.HSRConfig;

import ch.qos.logback.classic.Level;

/***************************************************************************
 * Simple debug test class for debugging usecases by running them once.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestDebugOnce extends PFRTest {

	public PFRTestDebugOnce() {
		
		//------------------------------
		// Test Settings
		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(90));
		
		//------------------------------
		// Settings
		Globals.commonInitialization(false);
		
		HSRConfig.setLogLevelRoot(Level.INFO); // or any other level
		PFRHttp.debugLogAll(true); 
		
		HSRConfig.setRawDataLogPath("./target/raw.log"); // debug only, performance impact with load!
		
		//------------------------------
		// Use Cases
		this.add( new PFRExecOnce(UsecaseLoadDataCustom.class, 0) ); // wait for 0 seconds
		

		
	}

}
