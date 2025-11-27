# Plugin: Performator HTTP
Following is the documentation of the Performator HTTP plugin.
 

# Basics

### Maven Dependency
The plugin is provided as a Maven dependency that can be defined as follows:

```java
<!-- https://mvnrepository.com/artifact/com.performetriks/performator-http -->
<dependency>
    <groupId>com.performetriks</groupId>
    <artifactId>performator-http</artifactId>
    <version>#.#.#</version>
</dependency>
);
```

### Classes
Following are the most important classes of the HTTP plugin:
* **PFRHttp:** The main class that hold configuration and allows to create HTTP requests with the PFRHttpRequestBuilder.
* **PFRHttpReponse:** The class that is returned for a HTTP response with various useful functions.

### Debug Logs
Following methods allow to enable debug logs for the current user(thread).

```java
PFRHttp.debugLogFail(true);	// write debug logs for failing requests
PFRHttp.debugLogAll(true);		// write debug logs for all requests
```

You can manually print debug logs for a response using `PFRHttpResponse.printDebugLog()`:

```java
PFRHttpResponse r = 
	PFRHttp.create("myMetricName", "www.mycompany.net/home")
			 .send();
			 
r.printDebugLog();		
```




### Default Timeout
Following method sets the default response timeout for the current user(thread).
The default value is 3 minutes when this is not set manually.

```java
public void initializeUser() {
	// set default HTTP timeout to 60 seconds
	PFRHttp.defaultResponseTimeout(HSRTimeUnit.s.toMillis(60)); 
}
```

**Note:** If you want to set this for all your use cases, you can call above method in the constructor of your PFRTest class.

### Default Pause
Following method sets the default pause after each request for the current user(thread).
By default there is no pause between requests.

```java
public void initializeUser() {
	// Wait 100 to 500 ms after each request to add some randomity 
	PFRHttp.defaultPause(100, 500); 
	// or just a static pause: PFRHttp.defaultPause(100); 
}
```
**Note:** If you want to set this for all your use cases, you can call above method in the constructor of your PFRTest class.

### Cookie Management
You can manage cookies for the current user(thread) by using the following methods:

```java
//=======================================
// Cookies
PFRHttp.clearCookies();			// Makes sure we always start with a blank user session
PFRHttp.addCookie(new BasicClientCookie("myCustomCookie", "baked-20-minutes-at-230-degrees-celsius"));
```


# Request Examples
Here are various request examples that show different functionalities.
The code piece `PFRHttpResponse r = null;` is included for completion reasons and making the examples easier to copy and paste.
In most use cases you will only declare one variable to capture the response for keeping the code more concise. Hence, the examples
here also do that for the sake of the guy who is writing this document and tries to optimize even such small details.

### GET Request
A simple executor that executes a use case once, useful for debugging, checks or functional tests.

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
			 .send()
			 ;
```

GET is the default method, in case you need to control it programmatically, you can also call the GET method:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.GET()
				.send()
				;
```

### POST Request
Use the POST()-Method to add a post request:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.POST()
				.send()
				;
```

### Request Parameters
Use the method `param(key, value)` to add request parameters:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.POST()
				.param("username", "admin")
				.param("password", "admin")
				.send()
				;
```

Alertnatively, you can use the method `params(map)` to add a whole list of parameters:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.POST()
				.params(myParameterMap)
				.send()
				;
```

### Request Headers
Use the method `header(key,value)` to add request headers:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.POST()
				.header("myHeaderKey", "myHeaderValue")
				.header("MyTracingID", "T1r4m15u-15-4we50me")
				.send()
				;
```

Alternatively, you can use the method `headers(map)` to add a whole list of parameters:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.POST()
				.headers(myHeadersMap)
				.send()
				;
```


### Request Timeout

Override the the default response time out with a custom timeout using the method `timeout(millis)`:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.timeout(10000) //  response timeout of 10 seconds
				.send()
				;
```

### Request Pause

Override the the default response time out with a custom timeout using the method `pause(millis)` or `pause(lowerMillis, upperMillis)`:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/tiramisu") 
				.pause(500) // adjust the pause for this request, default set with PFRHttp.defaultPause() 
				.send()
				;
```

### Break on Fail

If you want to break a test when a requests fails, you can use the method `PFRHttpResponse.throwOnFail()`.
Following is a concise way of doing so:

```java

PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
			.send()
			.throwOnFail() // break iteration here if not successful
			;
```

### Define Standard Checks
Add a check for the response using any of the methods `check*()`.
You can add as many as you want. If any of the checks fail, the request will be get the status "FAILED".

```java
r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.checkBodyContains("\"success\": true")
				.checkBodyContainsNot("Sign In")
				.checkBodyRegex("Name: .* Smith")
				.checkHeaderEquals("Content-Type", "application/json")
				.checkStatusEquals(206) // 206 Partial Content
				.check( // manual definition
						new PFRHttpCheck(CheckType.CONTAINS)
							.checkBody("containsThis")
							.appendLogDetails(true) 
							.messageOnFail("AAAH! HEEELLPP!!!!")
				)
				.send()
				;
```
### Implement Custom Checks
Anything you cannot handle with a standard check, you can implement yourself by using the interface `PFRHttpCheckCustom`.

```java

PFRHttpCheckCustom customCheck = 
		new PFRHttpCheckCustom() {
			@Override
			public boolean check(PFRHttpCheck check, PFRHttpResponse r) {
				check.messageOnFail("Body was smaller than 1KB.");
				return r.getBodySize() >= 1024;
			}
		}
		
r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.check(new PFRHttpCheck(customCheck))
				.send()
				;
```

### Define SLA
Add a SLA check using the method `sla(slaDefinition)`:

```java

private static final HSRSLA SLA_P90_AND_FAILRATE = 
			new HSRSLA(HSRMetric.p90, Operator.LTE, 500) // p90 <= 500ms
				  .and(HSRMetric.failrate, Operator.LTE, 5); // failure rate <= 5%
	
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.sla(SLA_P90_AND_FAILRATE)
				.send()
				;
```

### Disable Follow Redirects
The framework automatically follows HTTP redirects for your convenience.
This might get in the way for certain scenarios.
To disable this behavior use the method `disableFollowRedirects()`:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.disableFollowRedirects()
				.send()
				;
```

### Testing of Failing Requests
By default, a request is considered failed when the HTTP response status is >= 400.
This might get in the way for certain cases where you want to test failing requests.
To disable the automatic failing of requests use the method `allowHTTPErrors()`:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.allowHTTPErrors() // disables auto-fail when you want to test pages that return HTTP status >= 400
				.checkStatusEquals(405) // HTTP 405: Method Not Allowed
				.checkBodyRegex(".*Error.*")
				.send()
				;
				
```

# Extracting Values from Responses
You can use whatever methods or libraries you want to extract information from responses.
Here are some examples using what comes built-in with Performator.

### Extract Bounds
Following example shows you how to extract a list of IDs from a JSON response using `PFR.Text.extractBounds()`:

```java
//-------------------------------
// Extract Bounds example
ArrayList<String> ids = PFR.Text.extractBounds("\"PK_ID\":", ",", r.getBody());
logger.info("List of IDs: "+ String.join(", ", ids));
```

### Extract Regex Group
Following example shows you how to extract a list of Names from a JSON response using `PFR.Text.extractRegexAll()`:

```java
//-------------------------------
// Extract Regex example
ArrayList<String> names = PFR.Text.extractRegexAll("\"NAME\":\"(.*?)\",", 0, r.getBody());
logger.info("List of Names: "+ String.join(", ", names));
```

### Get JsonArray
Here is how you can get the response as a JsonObject and retrieve its "payload" as a JsonArray:

```java
//-------------------------------
// Working with Json
JsonObject object = r.getBodyAsJsonObject();
JsonArray dashboardArray = object.get("payload").getAsJsonArray();
//logger.info("List of Dashboards: "+ PFR.JSON.toJSONPretty(dashboardArray));
```

# Error Handling

### Try-Catch with throwOnFail()
Following is a typical structure how to handle failing requests:
1. By setting `PFRHttp.debugLogFail(true)` we get debug logs for all failing requests. (useful when there aren't too many failing requests)
2. Requests except Login and Logout call the method `throwOnFail()` and throw an exception when a request was not successful.
3. Try-catch will help skipping any other requests, while the the catch-block ensures the logout-step is called to not have any death sessions.

```java

private String url;

/************************************************************************
 * 
 ************************************************************************/
@Override
public void initializeUser() {
	url = Globals.ENV.url;
	PFRHttp.debugLogFail(true);		// log details for requests that fail
}

/************************************************************************
 * 
 ************************************************************************/
public void execute() throws Throwable {
	
	try {
		
		PFRHttpResponse r = null;
		
		//=======================================
		// Simple GET Request
		r = PFRHttp.create("000_Open_LoginPage", url+"/app/login") 
				.send()
				;
		
		// custom handlings
		if( !r.isSuccess() ) { return; } 
		
		//=======================================
		// POST with params
		r = PFRHttp.create("010_Do_Login", url+"/app/login") 
				.param("user", "admin")
				.param("pw", "admin")
				.send()
				.throwOnFail() // break iteration here if not successful
				;
		
		//=======================================
		// POST with params
		r = PFRHttp.create("020_Load_Dashboard", url+"/app/dashboard") 
				.send()
				.throwOnFail() // break iteration here if not successful
				;
		
		//=======================================
		// 
		doLogout();
		
	}catch(ResponseFailedException e) {
		// Custom logging if you don't want to use PFRHttp.debugLogFail(true);
		e.getResponse().printDebugLog();

		doLogout();
	}
}

/************************************************************************
 * 
 ************************************************************************/
private void doLogout(){
	PFRHttpResponse r;
	r = PFRHttp.create("999_Do_Logout", url+"/app/logout") 
			.GET()
			.checkBodyContains("Sign In")
			.send();
	
}
```


