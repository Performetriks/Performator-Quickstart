package com.performetriks.performator.quickstart.usecase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.slf4j.LoggerFactory;

import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.data.PFRDataRecord;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.http.PFRHttp.PFRHttpAuthMethod;
import com.performetriks.performator.http.PFRHttpCheck;
import com.performetriks.performator.http.PFRHttpCheck.PFRHttpCheckCustom;
import com.performetriks.performator.http.PFRHttpResponse;
import com.performetriks.performator.quickstart.globals.Globals;
import com.xresch.hsr.stats.HSRExpression.Operator;
import com.xresch.hsr.stats.HSRRecordStats.HSRMetric;
import com.xresch.hsr.base.HSR;
import com.xresch.hsr.stats.HSRSLA;
import com.xresch.hsr.utils.ByteSize;
import com.xresch.hsr.utils.HSRText.CheckType;

import ch.qos.logback.classic.Logger;

public class UsecaseExampleHTTPOverview extends PFRUsecase {

	private static final HSRSLA SLA_P90_AND_FAILRATE = 
			new HSRSLA(HSRMetric.p90, Operator.LTE, 500) // p90 <= 500ms
				  .and(HSRMetric.failrate, Operator.LTE, 5); // failure rate <= 5%
	
	private static Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(UsecaseExampleHTTPOverview.class.getName());
	
	private String url;
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void initializeUser() {
		url = Globals.ENV.url;
		
	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
		
		//=======================================
		// Load Test Data Record

		PFRDataRecord data = Globals.DATA.next();

		String id = data.get("ID").getAsString();
		String user = data.get("USER").getAsString();
		int value = data.get("VALUE").getAsInteger();
		boolean likesTiramisu = data.get("LIKES_TIRAMISU").getAsBoolean();

		PFRHttpResponse r = null;
		
		//==============================================================
		// Simple Overview of close to all the methods and what they do.
		// This request will not work, it's just an easy to provide an
		// overview to make it easier to find what you need.
		r = PFRHttp.create("000_My_Example_Metric_Name", url+"/app/example?id="+PFRHttp.encode(id)) 
				.sla(SLA_P90_AND_FAILRATE)				// add a reusable SLA 
				.sla(HSRMetric.avg, Operator.LTE, 100)	// add an SLA directly
				.sla(HSRMetric.p90, Operator.LTE, new BigDecimal(1.50))	// add an SLA  directly
				.sla(HSRMetric.max, Operator.LTE, 3.5)	// add an SLA Definition
				.GET()								// set the request method to GET
				.POST()								// set the request method to POST
				.PUT()								// set the request method to PUT
				.DELETE()							// set the request method to DELETE
				.METHOD("HEAD")						// set the request method the specified HTTP request method
				.params(getParameters(data))		// add multiple request parameters
				.param("username", user)			// add a request parameter, might override existing
				.headers(defaultHeaders())			// add multiple request headers
				.header("Accept-Language", "de")	// add a request header, might override existing
				.body("my request body")			// add a request body without content type
				.body("value", "text/plain")		// add a request body with a custom content type
				.bodyJSON("{\"key\": \"value\"}")	// add a request body with content type "application/json; charset=UTF-8"
				.setAuthCredentialsBasic("user", "pw") 								// set credentials for basic authentication
				.setAuthCredentials(PFRHttpAuthMethod.BASIC, "user", "pw") 			// same as above
				.setAuthCredentials(PFRHttpAuthMethod.BASIC_HEADER, "user", "pw") 	// set a basic authentication header with the specified credentials
				.setAuthCredentials(PFRHttpAuthMethod.DIGEST, "user", "pw") 		// set a credentials for digest authentication
				.setAuthCredentials(PFRHttpAuthMethod.KERBEROS, "user", "pw") 		// Experimental: Set a credentials for Kerberos authentication
				.setAuthCredentials(PFRHttpAuthMethod.NTLM, "user", "pw") 			// Experimental: Set a credentials for NTLM authentication
				.timeout(1000)						// adjust response timeout for this request, default set with PFRHttp.defaultResponseTimeout()
				.pause(200)   						// adjust pause for this request, default set with PFRHttp.defaultPause()
				.pause(100, 500)   					// adjust randomized pause for this request, default set with PFRHttp.defaultPause()
				.measureRange(value, 10)			// creates buckets for ranges based on a value
				.measureRange("-MyCount", value, 10)// creates buckets for ranges based on a value, add the suffix "-MyCount"
				.measureSize(ByteSize.KB)			// Measure response body size in kilobytes
				.allowHTTPErrors() 					// disables auto-fail when you want to test pages that return HTTP status >= 400
				.disableFollowRedirects()			// do not automatically follow redirects
				.checkStatusEquals(200) 			// check if response status is equals a certain status code
				.checkBodyContains("Sign In")		// check if response body contains a string
				.checkBodyContainsNot("failed")		// check if response body doesn't contain a string
				.checkBodyEquals("true")			// check if response body is equals a string
				.checkBodyRegex("\"payload\"")		// check if response body matches a regular expression
				.checkHeaderContains("Content-Encoding", "Sign In")		// check if response header contains a string
				.checkHeaderContainsNot("Cache-Control", "max-age")		// check if response header doesn't contain a string
				.checkHeaderEquals("Cache-Control", "no-cache")			// check if response header is equals a string
				.checkHeaderRegex("Content-Type", "application/.*")		// check if response header matches a regular expression
				// customized check
				.check(
					new PFRHttpCheck(CheckType.DO_NOT_MATCH_REGEX) 		// CONTAINS, DOES_NOT_CONTAIN, STARTS_WITH, ENDS_WITH, EQUALS, NOT_EQUALS, MATCH_REGEX, DO_NOT_MATCH_REGEX
						.checkBody(".*(Error|Fail).*")					// or .checkHeader() or .checkBody()
						.appendLogDetails(false) 						// if log details should be added to logs, default is true			
						.messageOnFail("my custom message")				// override default fail message
				)
				// super customized check
				.check(new PFRHttpCheck(new PFRHttpCheckCustom() {
						
						@Override
						public boolean check(PFRHttpCheck check, PFRHttpResponse r) {
							check.messageOnFail("Body was smaller than 1KB.");
							return r.getBodySize() >= 1024;
						}
					}) 
				)
				.send()					// Send the request
				.throwOnFail() 			// break iteration here if not successful by throwing an exception
				;
			
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	private HashMap<String,String> getParameters(PFRDataRecord record)  {
		
		HashMap<String,String> parameters = new LinkedHashMap<String,String>();
		
		parameters.put("value", record.get("VALUE").getAsString());
		parameters.put("firstname", record.get("FIRSTNAME").getAsString());
		parameters.put("lastname", record.get("LASTNAME").getAsString());
		parameters.put("search", record.get("SEARCH_FOR").getAsString());
		
		return parameters;
		
	}
	/************************************************************************
	 * 
	 ************************************************************************/
	private HashMap<String,String> defaultHeaders()  {
		
		HashMap<String,String> headers = new LinkedHashMap<String,String>();
		
		headers.put("Cache-Control", "no-cache");
		headers.put("Origin", url);
		headers.put("x-dynatrace", "PerformatorTest");
		
		return headers;
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate() {
		// nothing todo
	}

}
