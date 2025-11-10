package com.performetriks.performator.test;

import org.junit.jupiter.api.Test;

import com.performetriks.performator.base.PFRCoordinator;
import com.performetriks.performator.quickstart.tests.PFRTestExample;

/***************************************************************************
 * This is an example on how to programmatically execute a test on the 
 * local machine using JUnit, without the need to execute the JAR file 
 * with command line arguments.
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: Eclipse Public License v2.0
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class TestExecuteProgrammatically {

	/*****************************************************************
	 * 
	 *****************************************************************/
	@Test
	void testCoordinator() {
		
		// IMPORTANT: This will only execute locally!
		PFRCoordinator.startTest(PFRTestExample.class.getName());
		
	}
	

}