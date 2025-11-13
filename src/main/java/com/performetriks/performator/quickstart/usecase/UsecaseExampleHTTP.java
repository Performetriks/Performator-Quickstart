package com.performetriks.performator.quickstart.usecase;

import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.slf4j.LoggerFactory;

import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRContext;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.http.PFRHttpResponse;
import com.performetriks.performator.http.ResponseFailedException;
import com.performetriks.performator.quickstart.globals.Globals;

import ch.qos.logback.classic.Logger;

public class UsecaseExampleHTTP extends PFRUsecase {

	private static Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(UsecaseExampleHTTP.class.getName());
	
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

		String user = PFR.Random.fromStrings("Alejandra", "Mariachetta", "Jettera");
		String data = PFR.Random.fromStrings("X", "Y", "Z");
		
		//-------------------------------
		// Configuration
		PFRHttp.clearCookies();			// Makes sure we always start with a blank user session
		PFRHttp.addCookie(new BasicClientCookie("myCustomCookie", "baked-20-minutes-at-230-degrees-celsius"));
		PFRHttp.debugLogFail(true);		// log details for requests that fail
		//PFRHttp.debugLogAll(true); 	// log all request details
		PFRContext.logDetailsAdd("user", user); // add custom details to logs, very useful to find failing test data
		PFRContext.logDetailsAdd("data", data); 
		try {
			
			PFRHttpResponse r = null;
			
			//-------------------------------
			// 
			r = PFRHttp.create("000_Open_LoginPage", url+"/app/login") 
					.GET()
					.checkBodyContains("Sign In")
					.send()
					;
			
			if( !r.isSuccess() ) { return; } // custom handling
			
			//-------------------------------
			// 
			r = PFRHttp.create("010_Do_Login", url+"/app/login") 
					.POST()
					.param("username", "admin")
					.param("password", "admin")
					.param("url", "/app/dashboard/list") //redirect url
					.checkBodyContains("cfwMenuTools-Dashboards")
					.checkBodyContainsNot("Sign In")
					.send()
					.throwOnFail()
					;
			
			//-------------------------------
			// 
			r = PFRHttp.create("020_Load_DashboardList", url+"/app/dashboard/list?action=fetch&item=mydashboards") 
					.POST()
					.checkBodyContains("\"success\": true")
					.send()
					.throwOnFail()
					;

			//-------------------------------
			// 
			// This will always fail for demo purposes
			r = PFRHttp.create("030_UnkownPage", url+"/app/doesNotExist") 
					.POST()
					.allowHTTPErrors() // disables auto-fail when you want to test pages that return HTTP status >= 400
					.checkStatusEquals(405) // Method Not Allowed
					.checkBodyRegex(".*Error.*")
					.send()
					
					;
			//-------------------------------
			// 
			doLogout(true);
			
		}catch(ResponseFailedException e) {
			doLogout(false);
		}
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	private void doLogout(boolean throwOnFail) throws ResponseFailedException {
		PFRHttpResponse r;
		r = PFRHttp.create("999_Do_Logout", url+"/app/logout") 
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
