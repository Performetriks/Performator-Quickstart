package com.performetriks.performator.quickstart.usecase;

import java.math.BigDecimal;

import com.performetriks.performator.base.PFRContext;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.http.PFRHttp;
import com.performetriks.performator.quickstart.globals.Globals;
import com.xresch.hsr.base.HSR;
import com.xresch.hsr.stats.HSRRecord.HSRRecordStatus;

public class UsecaseExampleHTTP extends PFRUsecase {

	
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
		
		//-------------------------------
		// 
		PFRHttp.create("01_Homepage", url) 
				.send()
				;
		
	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate(PFRContext context) {
		// nothing todo
	}

}
