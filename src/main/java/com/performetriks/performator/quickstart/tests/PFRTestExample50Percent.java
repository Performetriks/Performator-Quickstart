package com.performetriks.performator.quickstart.tests;

import com.performetriks.performator.executors.PFRExec;
import com.performetriks.performator.executors.PFRExecStandard;

/***************************************************************************
 * An example on how you can adapt a test to a certain percentage, without
 * having to copy the whole scenario.
 * Extend the test class you want to adapt and iterate over the scenario.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestExample50Percent extends PFRTestExample {
										  // ^^^^^^^^^^^^^^
										  // We are extending the scenario that we cant to adapt
	
	public PFRTestExample50Percent() {
		
		super();
		
		//---------------------------
		// Iterate over all executors 
		// and set percentage
		for(PFRExec exec : this.getExecutors()) {
			if(exec instanceof PFRExecStandard) {
				((PFRExecStandard)exec).percent(50);
			}
		}
		
	}

}
