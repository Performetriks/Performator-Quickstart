package com.performetriks.performator.quickstart.usecase;

import org.slf4j.LoggerFactory;

import com.performetriks.performator.base.PFRContext;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.http.PFRHttp.Response;
import com.performetriks.performator.quickstart.globals.Globals;

import ch.qos.logback.classic.Logger;

public class UsecaseExampleHTTP extends PFRUsecase {

	private static Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(UsecaseExampleHTTP.class.getName());
	
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void initializeUser(PFRContext context) {
		// TODO Auto-generated method stub
		// nothing todo
	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void execute(PFRContext context) throws Throwable {

		String url = Globals.ENV.url;
		Response r = null;
		
		// PFRHttp.debugLogAll(true);
		// PFRHttp.debugLogFail(true);
		
		//-------------------------------
		// 
		r = PFRHttp.create("000_Open_LoginPage", url+"/app/login") 
				.GET()
				.checkBodyContains("Sign In")
				.send()
				;
		
		if( !r.isSuccess() ) { return; }
		
		//-------------------------------
		// 
		r = PFRHttp.create("010_Do_Login", url+"/app/login") 
				.POST()
				.param("username", "admin")
				.param("password", "admin")
				.param("url", "/app/dashboard/list") //redirect url
				.checkBodyContains("cfwMenuTools-Dashboards")
				.send()
				;
		
		//-------------------------------
		// 
		r = PFRHttp.create("999_Do_Logout", url+"/app/logout") 
				.GET()
				.checkBodyContains("Sign In")
				.send()
				;
		
		if( !r.isSuccess() ) { return; }
		
	}
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate(PFRContext context) {
		// nothing todo
	}

}
