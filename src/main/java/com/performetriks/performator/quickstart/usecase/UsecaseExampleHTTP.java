package com.performetriks.performator.quickstart.usecase;

import java.util.ArrayList;

import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRContext;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.data.PFRDataRecord;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.http.PFRHttpCheck;
import com.performetriks.performator.http.PFRHttpCheck.PFRHttpCheckCustom;
import com.performetriks.performator.http.PFRHttpResponse;
import com.performetriks.performator.http.ResponseFailedException;
import com.performetriks.performator.quickstart.globals.Globals;
import com.xresch.hsr.stats.HSRExpression.Operator;
import com.xresch.hsr.stats.HSRRecordStats.HSRMetric;
import com.xresch.hsr.stats.HSRSLA;
import com.xresch.hsr.utils.ByteSize;
import com.xresch.hsr.utils.HSRTime.HSRTimeUnit;

import ch.qos.logback.classic.Logger;

public class UsecaseExampleHTTP extends PFRUsecase {

	private static final HSRSLA SLA_P90_AND_FAILRATE = 
			new HSRSLA(HSRMetric.p90, Operator.LTE, 500) // p90 <= 500ms
				  .and(HSRMetric.failrate, Operator.LTE, 5); // failure rate <= 5%
	
	private static Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(UsecaseExampleHTTP.class.getName());
	
	private String url;
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void initializeUser() {
		url = Globals.ENV.url;
		
		PFRHttp.defaultResponseTimeout(HSRTimeUnit.s.toMillis(60)); // set default HTTP timeout to 60 seconds
		PFRHttp.debugLogFail(true);		// log details for requests that fail
		//PFRHttp.debugLogAll(true); 	// log all request details
	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
		
		//=======================================
		// Load Test Data Record
		if( ! Globals.DATA.hasNext() ) {
			logger.warn(this.getName()+": No test data available.");
			return;
		}
		
		PFRDataRecord record = Globals.DATA.next();

		String user = record.get("USER").getAsString();
		int value = record.get("VALUE").getAsInteger();
		boolean likesTiramisu = record.get("LIKES_TIRAMISU").getAsBoolean();
//		JsonObject addressDetails = record.get("ADDRESS_DETAILS").getAsJsonObject();
//		JsonArray tags = record.get("TAGS").getAsJsonArray();
		
		//logger.info(record.toString()); // how to log test data
		
		//=======================================
		// Configuration
		PFRHttp.clearCookies();			// Makes sure we always start with a blank user session
		PFRHttp.addCookie(new BasicClientCookie("myCustomCookie", "baked-20-minutes-at-230-degrees-celsius"));
		
//		PFRContext.logDetailsAdd("user", user); // add custom details to logs, very useful to find failing test data
//		PFRContext.logDetailsAdd("searchFor", searchFor); 
		PFRContext.logDetailsAdd(record); // add all the fields of the data record
		
		try {
			
			PFRHttpResponse r = null;
			
			//=======================================
			// Simple GET Request
			r = PFRHttp.create("000_Open_LoginPage", url+"/app/login") 
					.sla(SLA_P90_AND_FAILRATE)
					.GET()
					.checkBodyContains("Sign In")
					.send()
					;
			
			if( !r.isSuccess() ) { return; } // custom handling
			
			//=======================================
			// POST with params
			r = PFRHttp.create("010_Do_Login", url+"/app/login") 
					.sla(SLA_P90_AND_FAILRATE)
					.POST()
					.param("username", "admin")
					.param("password", "admin")
					.param("url", "/app/dashboard/list") //redirect url
					.checkBodyContains("cfwMenuTools-Dashboards")
					.checkBodyContainsNot("Sign In")
					//.disableFollowRedirects()
					.send()
					.throwOnFail() // break iteration here if not successful
					;
							
			//=======================================
			// JSON Request
			r = PFRHttp.create("020_Load_DashboardList", url+"/app/dashboard/list?action=fetch&item=mydashboards") 
					.sla(SLA_P90_AND_FAILRATE)
					.POST()
					.timeout(1000) // adjust response timeout for this request
					.measureSize(ByteSize.KB)
					.checkBodyContains("\"success\": true")
					.checkBodyRegex("\"payload\"")
					// super customized check
					.check(new PFRHttpCheck(new PFRHttpCheckCustom() {
							
							@Override
							public boolean check(PFRHttpCheck check, PFRHttpResponse r) {
								check.messageOnFail("Body was smaller than 1KB.");
								return r.getBodySize() >= 1024;
							}
						}) 
					)
					.send()
					.throwOnFail()
					;
			
				//-------------------------------
				// Manual Printing of Debug Log
				r.printDebugLog();
				
				//-------------------------------
				// Extract Bounds example
				ArrayList<String> ids = PFR.Text.extractBounds("\"PK_ID\":", ",", r.getBody());
				logger.info("List of IDs: "+ String.join(", ", ids));
				
				//-------------------------------
				// Extract Regex example
				ArrayList<String> names = PFR.Text.extractRegexAll("\"NAME\":\"(.*?)\",", 0, r.getBody());
				logger.info("List of Names: "+ String.join(", ", names));
				
				//-------------------------------
				// Working with Json
				JsonObject object = r.getBodyAsJsonObject();
				JsonArray dashboardArray = object.get("payload").getAsJsonArray();
				//logger.info("List of Dashboards: "+ PFR.JSON.toJSONPretty(dashboardArray));
				
			
			//=======================================
			// Testing Failing Requests
			r = PFRHttp.create("030_UnkownPage", url+"/app/doesNotExist") 
					.sla(SLA_P90_AND_FAILRATE)
					.POST()
					.allowHTTPErrors() // disables auto-fail when you want to test pages that return HTTP status >= 400
					.checkStatusEquals(405) // HTTP 405: Method Not Allowed
					.checkBodyRegex(".*Error.*")
					.send()
					;
			
			//=======================================
			// 
			doLogout(true);
			
		}catch(ResponseFailedException e) {
			// Custom logging if you don't want to use PFRHttp.debugLogFail(true);
			//e.getResponse().printDebugLog();

			doLogout(false);
		}
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	private void doLogout(boolean throwOnFail) throws ResponseFailedException {
		PFRHttpResponse r;
		r = PFRHttp.create("999_Do_Logout", url+"/app/logout") 
				.sla(SLA_P90_AND_FAILRATE)
				.GET()
				.checkBodyContains("Sign In")
				.send();
		
		if(throwOnFail) {
			r.throwOnFail();
		}
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate() {
		// nothing todo
	}

}
