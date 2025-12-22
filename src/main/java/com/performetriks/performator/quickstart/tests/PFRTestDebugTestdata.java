package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecRepeat;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.usecase.UsecaseCheckTestdata;

/***************************************************************************
 * Simple debug test class for checking if the test data is working.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestDebugTestdata extends PFRTest {

	public PFRTestDebugTestdata() {
		
		Globals.commonInitialization(false);
		
		int recordCount = Globals.DATA.size();

		// iterate every test data that is in the file
		// works when the data is read sequentially
		this.add( new PFRExecRepeat(UsecaseCheckTestdata.class, recordCount) ); // wait for 0 seconds

		this.maxDuration(Duration.ofHours(9999)); // run until executor finished

		
	}

}
