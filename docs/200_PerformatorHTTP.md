# Performator HTTP Addon
Following is the documentation of the Performator HTTP addon.
 

# Basics

### Maven Dependency
The addon is provided as a maven dependency that can be defined as follows:

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
Following are the most important classes of the HTTP addon:
* **PFRHttp:** The main class that hold configuration and allows to create HTTP requests with the PFRHttpRequestBuilder.
* **PFRHttpReponse:** The class that is returned for a HTTP response with various useful functions.

### Debug Logs
Following methods allow to enable debug logs for the current user(thread).

```java
PFRHttp.debugLogFail(true);	// write debug logs for failing requests
PFRHttp.debugLogAll(true);		// write debug logs for all requests
```

### Default Timeout
Following method sets the default response timeout for the current user(thread).

```java
PFRHttp.defaultResponseTimeout(Duration.ofSeconds(60).toMillis());
```

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
				.check( // customized
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
The framework outomatically follows HTTP redirects for your convenience.
This might get in the way for certain scenarios.
To disable this behavior use the method `disableFollowRedirects()`:

```java
PFRHttpResponse r = null;

r = PFRHttp.create("010_MyMetricName", "www.mycompany.net/home") 
				.disableFollowRedirects()
				.send()
				;
```




