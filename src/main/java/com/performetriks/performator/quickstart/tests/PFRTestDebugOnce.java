package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecOnce;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHTTP;
import com.xresch.hsr.base.HSRConfig;

public class PFRTestDebugOnce extends PFRTest {

	public PFRTestDebugOnce() {
		
		Globals.commonInitialization(false);
		
		HSRConfig.setRawDataLogPath("./target/raw.log"); // debug only, performance impact with load!
		
		this.add( new PFRExecOnce(UsecaseExampleHTTP.class, 0) ); // wait for 0 seconds
		
		this.maxDuration(Duration.ofSeconds(90));
		this.gracefulStop(Duration.ofSeconds(90));
		
	}

}
