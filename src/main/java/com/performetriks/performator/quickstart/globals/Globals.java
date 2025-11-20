package com.performetriks.performator.quickstart.globals;

import org.slf4j.LoggerFactory;

import com.performetriks.performator.base.PFR;
import com.performetriks.performator.data.PFRDataSource;
import com.performetriks.performator.data.PFRDataSource.AccessMode;
import com.performetriks.performator.data.PFRDataSource.RetainMode;
import com.xresch.hsr.base.HSRConfig;
import com.xresch.hsr.reporting.HSRReporterCSV;
import com.xresch.hsr.reporting.HSRReporterDatabasePostGres;
import com.xresch.hsr.reporting.HSRReporterHTML;
import com.xresch.hsr.reporting.HSRReporterJson;
import com.xresch.hsr.reporting.HSRReporterSysoutCSV;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


public class Globals {
	 
	private static final String PACKAGE_DATA = "com.performetriks.performator.quickstart.data";
	
	public static final String DIR_RESULTS = "./target";
	public static final int REPORT_INTERVAL_SECONDS = 15;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(Globals.class);

	//================================================================
	// Define your Environments here
	//================================================================
	public enum Environment {
		
		  DEV ("http://localhost:8888")
		, TEST("http://localhost:7777")
		;

		public final String url;
		
		Environment(String url) {
			this.url = url;
		}
				
		// example on how to keep things in one place
		public String getTestdataPackage() { return PACKAGE_DATA + "."+this.toString(); }
		public String getAPIURL() { return url + "/rest/api"; }
		public String getXDynatraceHeader() { return "PerformatorTest"; }
		
	}
	
	//================================================================
	// Set the Environment to run your Test against
	//================================================================
	public static Environment ENV = Environment.DEV;
	
	
	public static PFRDataSource DATA;
	
	/************************************************************************
	 * 
	 ************************************************************************/
	public static void commonInitialization() {
		
		//--------------------------
		// Load Test Data CSV	
		DATA = PFR.Data.newSourceCSV("mainTestdataCSV", ENV.getTestdataPackage(), "testdata.csv", ",")
				.accessMode(AccessMode.SEQUENTIAL)
				.retainMode(RetainMode.INFINITE)
				.build();
		;
		
		//--------------------------
		// Load Test Data JSON
//		DATA = PFR.Data.newSourceJson("mainTestdataJSON", ENV.getTestdataPackage(), "testdata.json")
//						.accessMode(AccessMode.SEQUENTIAL)
//						.retainMode(RetainMode.ONCE)
//						.build();
//						;
		
		//--------------------------
		// Log Levels
		HSRConfig.setLogLevelRoot(Level.WARN);
		HSRConfig.setLogLevel(Level.INFO, "com.performetriks.performator");
		HSRConfig.setLogLevel(Level.INFO, "com.xresch.hsr");
		
		//--------------------------
		// Set Test Properties
		HSRConfig.addProperty("[Custom] Environment", "TEST");
		HSRConfig.addProperty("[Custom] Testdata Rows", "120");
		
		//--------------------------
		// Optional: Disabling System Usage Stats
//		HSRConfig.statsProcessMemory(false);
//		HSRConfig.statsHostMemory(false);
//		HSRConfig.statsCPU(false);
//		HSRConfig.statsDiskUsage(false);
//		HSRConfig.statsDiskIO();
//		HSRConfig.statsNetworkIO();
		
		//--------------------------
		// Optional: Log every datapoint
		// potential performance impact, debug use only
//		HSRConfig.setRawDataLogPath(DIR_RESULTS+"/raw.log");
		
		//--------------------------
		// Define Sysout Reporters
		HSRConfig.addReporter(new HSRReporterSysoutCSV(" | "));
		//HSRConfig.addReporter(new HSRReporterSysoutJson());
		
		//--------------------------
		// Define File Reporters
		HSRConfig.addReporter(new HSRReporterJson( DIR_RESULTS + "/hsr-stats.json", true) );
		HSRConfig.addReporter(new HSRReporterCSV( DIR_RESULTS + "/hsr-stats.csv", ",") );
		HSRConfig.addReporter(new HSRReporterHTML( DIR_RESULTS + "/HTMLReport") );
		
		//--------------------------
		// Database Reporters
		HSRConfig.addReporter(
			new HSRReporterDatabasePostGres(
				"localhost"
				, 5432
				, "postgres"	// dbname
				, "hsr"			// table name prefix
				, "postgres"	// user
				, "postgres"	// pw
			)
		);
		
    	//------------------------------
    	// EMP Reporter
//		HSRConfig.addReporter(
//    			new HSRReporterEMP(
//    					"http://localhost:8888"
//    					,"gatlytron-test-token-MSGIUzrLyUsOypYOkekVgmlfjMpLbRCA"
//    				)
//    			);
    	
    	//------------------------------
    	// JDBC DB Reporter
//		HSRConfig.addReporter(
//    			new HSRReporterDatabaseJDBC("org.h2.Driver"
//    					, "jdbc:h2:tcp://localhost:8889/./datastore/h2database;MODE=MYSQL;IGNORECASE=TRUE"
//    					, "hsr"
//    					, "sa"
//    					, "sa") {
//					
//					@Override
//					public HSRDBInterface getGatlytronDB(DBInterface dbInterface, String tableNamePrefix) {
//						return new HSRDBInterface(dbInterface, tableNamePrefix);
//					}
//
//					@Override
//					public void reportSummary(ArrayList<HSRRecordStats> summaryRecords,
//							JsonArray summaryRecordsWithSeries, TreeMap<String, String> properties,
//							JsonObject slaForRecords) {
//						// TODO Auto-generated method stub
//						
//					}
//				}
//    		);
		
		//--------------------------
		// Enable
		HSRConfig.enable(REPORT_INTERVAL_SECONDS); 
		
	}
}
