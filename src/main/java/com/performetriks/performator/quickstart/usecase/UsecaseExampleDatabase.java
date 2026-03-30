package com.performetriks.performator.quickstart.usecase;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.google.common.base.Joiner;
import com.performetriks.performator.base.PFR;
import com.performetriks.performator.base.PFRUsecase;
import com.performetriks.performator.database.PFRDB;
import com.xresch.hsr.base.HSR;
import com.xresch.hsr.stats.HSRRecord;
import com.xresch.xrutils.data.Unrecord;

public class UsecaseExampleDatabase extends PFRUsecase {

	private static PFRDB db = PFRDB.initDBInterfacePostgres("localhost"
			, 5432
			, "postgres"	// dbname
			, "postgres"	// user
			, "postgres"	// pw
		);
	
	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void initializeUser() {

	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void execute() throws Throwable {
		

		//------------------------------
		// Query and get Result Set
		ResultSet r = db.create() // no metricName = will use SQL as name
				.query( "SELECT * from hsr_stats LIMIT " + PFR.Random.from(1, 10, 100, 500, 1000, 10000) )
				.toResultSet()
				;
		
		// do something with the result set, then close
		db.close(r);
		

		//------------------------------
		// Query and get Unrecords
		ArrayList<Unrecord> tests = db.create()
							  .query("SELECT id, time, endtime from hsr_tests LIMIT 100")
							  .toUnrecordList()
							  ;
		
		Unrecord randomTest = PFR.Random.fromArray(tests);
		
		//------------------------------
		// Query with Ranged Metrics
		db.create()
			.enableRangedMetrics("#Stats", 100)
			.query("SELECT * from hsr_stats WHERE testid = ?", randomTest.getInteger("id") )
			.close()
			;

		
		//------------------------------
		// Custom Metric Name
		// If you don't want the SQL
		// as the metric name
		db.create("Aggregate Query") // use this as metric name instead of SQL
		  .query("""
		  	SELECT 
				      MIN("testid")
					, MIN("time") + ((MAX("time") - MIN("time"))/2) AS "time"
				    , "type","test","usecase","path","name","code"
				    , 999 AS "granularity"
				    
				, ( CASE
				       WHEN type IN ('System', 'User', 'Gauge') THEN AVG(ok_count)
				       ELSE SUM(ok_count)
				   END ) AS "ok_count"
				, MIN("ok_min") AS "ok_min"
				, AVG("ok_avg") AS "ok_avg"
				, MAX("ok_max") AS "ok_max"
				, STDDEV("ok_stdev") AS "ok_stdev"
				, PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY "ok_p25") AS "ok_p25"
				, PERCENTILE_CONT(0.50) WITHIN GROUP (ORDER BY "ok_p50") AS "ok_p50"
				, PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY "ok_p75") AS "ok_p75"
				, PERCENTILE_CONT(0.90) WITHIN GROUP (ORDER BY "ok_p90") AS "ok_p90"
				, PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY "ok_p95") AS "ok_p95"
				, PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY "ok_p99") AS "ok_p99"
				, ROUND(AVG("ok_sla")) AS "ok_sla"
				, ( CASE
				       WHEN type IN ('System', 'User', 'Gauge') THEN AVG(nok_count)
				       ELSE SUM(nok_count)
				   END ) AS "nok_count"
				, MIN("nok_min") AS "nok_min"
				, AVG("nok_avg") AS "nok_avg"
				, MAX("nok_max") AS "nok_max"
				, STDDEV("nok_stdev") AS "nok_stdev"
				, PERCENTILE_CONT(0.25) WITHIN GROUP (ORDER BY "nok_p25") AS "nok_p25"
				, PERCENTILE_CONT(0.50) WITHIN GROUP (ORDER BY "nok_p50") AS "nok_p50"
				, PERCENTILE_CONT(0.75) WITHIN GROUP (ORDER BY "nok_p75") AS "nok_p75"
				, PERCENTILE_CONT(0.90) WITHIN GROUP (ORDER BY "nok_p90") AS "nok_p90"
				, PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY "nok_p95") AS "nok_p95"
				, PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY "nok_p99") AS "nok_p99"
				, ROUND(AVG("nok_sla")) AS "nok_sla"
				, SUM("success") AS "success"
				, SUM("failed") AS "failed"
				, SUM("skipped") AS "skipped"
				, SUM("aborted") AS "aborted"
				, SUM("aborted") AS "none"
				, AVG("failrate") AS "failrate"
			FROM hsr_stats
			WHERE "testid" = ?
			AND	"time" >= ? 
			AND "time" < ? 
			AND "granularity" < 999
			GROUP BY "testid", "type","test","usecase","path","name","code"	
		"""
		, randomTest.getInteger("id")
		, randomTest.getInteger("time")
		, randomTest.getInteger("endtime")
		)
		.close();

		//====================================
		// Transaction
		//====================================
		
		String tempTableName = "temptable_"+PFR.Random.string(8);
		db.transactionStart();
		
			//------------------------------
			// Temp Table
			db.create("Temp Table: 010_Create")
				.execute("CREATE TABLE IF NOT EXISTS " + tempTableName + """ 
					(
						id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
					  , time BIGINT
					  , name VARCHAR(4096)
					  , description VARCHAR(65536)
					  , properties VARCHAR(65536)
					  , tags VARCHAR(65536)
					);
					"""
				)
				;
			
			//------------------------------
			// Insert
			int numberOfInserts = PFR.Random.from(1, 10, 100, 500, 1000);
			
			String insertGroupName = "Temp Table: 020_InsertGroup";
			HSR.startGroup(insertGroupName);
				for(int i = 0; i < numberOfInserts ; i++) {
				
					db.create("Temp Table: 030_Insert")
						.execute("INSERT INTO " + tempTableName + """ 
								(time, name, description, properties, tags)
								VALUES (?,?,?,?,?)
							"""
							, System.currentTimeMillis()
							, "Test Name"
							, "Test Description "+PFR.Random.loremIpsum(4096)
							, "{my properties}"
							, Joiner.on(", ").join( PFR.Random.arrayOfExaggaratingAdjectives(10))
						)
						;
				}
			HSRRecord record = HSR.end();
			
			HSR.addMetricRanged(
					insertGroupName + " - #Inserts"
				  , record.value()
				  , numberOfInserts
				  , 5
				);
			
			//------------------------------
			// Query with Ranged Metrics
			db.create("Temp Table: Read")
				.enableRangedMetrics("#Records", 100)
				.query("SELECT * from " + tempTableName)
				;
			
		db.transactionRollback();
	}

	/************************************************************************
	 * 
	 ************************************************************************/
	@Override
	public void terminate() {
		// nothing todo
	}

}
