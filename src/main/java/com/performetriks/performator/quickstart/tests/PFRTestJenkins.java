package com.performetriks.performator.quickstart.tests;

import java.time.Duration;

import com.performetriks.performator.base.PFRTest;
import com.performetriks.performator.executors.PFRExecStandard;
import com.performetriks.performator.quickstart.globals.Globals;
import com.performetriks.performator.quickstart.globals.Globals.Environment;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleHSR;
import com.performetriks.performator.quickstart.usecase.UsecaseExampleSLA;
import com.xresch.hsr.base.HSRConfig;

import ch.qos.logback.classic.Level;

/***************************************************************************
 * This example a basic test with two standard scenarios
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 ***************************************************************************/
public class PFRTestJenkins extends PFRTest {

	public PFRTestJenkins() {
		
		//--------------------------------------
    	// Get Jenkins Parameters
        int percent = Integer.getInteger("LoadPercent", 10);
        int durationMinutes = Integer.getInteger("Duration", 10);
        
        String logLevelString = System.getProperty("LogLevel", "INFO");
        Level logLevel = Level.toLevel(logLevelString);
        
        String environmentString = System.getProperty("Environment", "DEV");
        Environment environment = Environment.valueOf(environmentString);

    	//--------------------------------------
    	// Print Parameters
        System.out.println("============== Jenkins Parameters ==============");
        System.out.println("Environment:       " + environment);
        System.out.println("Workload Percent:  " + percent);
        System.out.println("Duration Minutes:  " + durationMinutes);
        System.out.println("Log Level:         " + logLevel);
        System.out.println("================================================");
        
    	//--------------------------------------
    	// Setup and Run Test

		Globals.jenkinsInitialization(true, environment);
		HSRConfig.setLogLevelRoot(logLevel);

		this.add(new PFRExecStandard(UsecaseExampleHSR.class, 50, 12000, 0, 5).percent(percent) );
		this.add(new PFRExecStandard(UsecaseExampleSLA.class)
						.users(5)
						.execsHour(2000)
						.rampUp(2) 
						.offset(20)
						.percent(percent)
					);
		
		this.maxDuration(Duration.ofMinutes(durationMinutes));
		this.gracefulStop(Duration.ofMinutes(1));
		
	}

}
