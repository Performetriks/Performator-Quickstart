package com.performetriks.performator.quickstart.usecase;

import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
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
import com.xresch.hsr.base.HSR;
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
		PFRHttp.defaultPause(100, 500); // Wait 100 to 500 ms after each request to add some randomity 
		
		PFRHttp.defaultThrowOnFail(true); // set that by default a request that fails will throw a ResponseFailedException 
		
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

		String user = record.getString("USER");
		int value = record.getInteger("VALUE");
		boolean likesTiramisu = record.getBoolean("LIKES_TIRAMISU");
//		JsonObject addressDetails = record.getJsonObject("ADDRESS_DETAILS");
//		JsonArray tags = record.getJsonArray("TAGS");
		
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
					.throwOnFail(false) // do not break iteration here if not successful
					.send()
					;
			
			// custom handling, do not call doLogout()
			if( !r.isSuccess() ) { 
				HSR.addErrorMessage("Login Failed: "+PFRContext.logDetailsString());
				return; 
			} 
			
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
					.throwOnFail(false) // do not break iteration here if not successful
					//.disableFollowRedirects()
					.send()
					;
			
			// custom handling, do not call doLogout()
			if( !r.isSuccess() ) { 
				HSR.addErrorMessage("Login Failed: "+PFRContext.logDetailsString());
				return; 
			} 
			//=======================================
			// JSON Request
			r = PFRHttp.create("020_Load_DashboardList", url+"/app/dashboard/list?action=fetch&item=mydashboards") 
					.sla(SLA_P90_AND_FAILRATE)
					.POST()
					.timeout(1000) // adjust response timeout for this request, default set with PFRHttp.defaultResponseTimeout()
					.pause(200)    // adjust pause for this request, default set with PFRHttp.defaultPause()
					.measureRange("(ByValue)", value, 10)
					.measureRange("-ByRandom", HSR.Random.integer(1, 10000), 100)
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
					;
			
				
				//-------------------------------
				// Manual Printing of Debug Log
				//r.printDebugLog();
				
				//-------------------------------
				// Measure Time by Range of 
				// Returned Data Count
				DocumentContext ctx = JsonPath.parse(r.getBody());
				Integer count = ctx.read("$.payload.length()");
				r.measureRange("[ByBoardCount]", count, 5);
				
				//-------------------------------
				// Extract using JsonPath
				
				List<Integer> allIDs = ctx.read("$.payload[*].PK_ID");
				List<String> allNames = ctx.read("$.payload[*].NAME");
				String firstName = ctx.read("$.payload[0].NAME");
				String lastName = ctx.read("$.payload[-1].NAME");
				Boolean thirdIsShared = ctx.read("$.payload[3].IS_SHARED");
				
				logger.info("JsonPath: allIDs: " + PFR.JSON.toJSON(allIDs));
				logger.info("JsonPath: allNames: " + PFR.JSON.toJSON(allNames));
				logger.info("JsonPath: firstName: " + firstName);
				logger.info("JsonPath: lastName: " + lastName);
				logger.info("JsonPath: thirdIsShared: " + thirdIsShared);
				
				//-------------------------------
				// Extract Bounds example
				ArrayList<String> ids = PFR.Text.extractBounds("\"PK_ID\":", ",", r.getBody());
				logger.info("extractBounds: List of IDs: "+ String.join(", ", ids));
				
				//-------------------------------
				// Extract Regex example
				ArrayList<String> names = PFR.Text.extractRegexAll("\"NAME\":\"(.*?)\",", 0, r.getBody());
				logger.info("extractRegexAll: List of Names: "+ String.join(", ", names));
				
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
			doLogout();
			
		}catch(ResponseFailedException e) {
			// Custom logging if you don't want to use PFRHttp.debugLogFail(true);
			//e.getResponse().printDebugLog();

			doLogout();
		}
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	private void doLogout() throws ResponseFailedException {
		PFRHttpResponse r;
		r = PFRHttp.create("999_Do_Logout", url+"/app/logout") 
				.sla(SLA_P90_AND_FAILRATE)
				.GET()
				.checkBodyContains("Sign In")
				.send();
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate() {
		// nothing todo
	}

}
