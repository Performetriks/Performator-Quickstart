
# Using AI
As an alternative to the HTTP Converter, you can use AI chatbots like ChatGPT, Grok or Claude to help you to generate Performator usecase classes from HAR files, Postman collections, curl commands and such.

**IMPORTANT:** Fast models can give bad results compared to models that think longer. If your AI has an option to select a model that puts more thoughts into its answers, you should select it before uploading the AGENT.md file.

To train the AI of your choice, we created the following file:

[AGENT.md](./AGENT.md)

Upload the file in your AI chat and add a prompt like:

```
I want you to be the agent explained in the file.
```

Your AI should now be able to receive your input and help you generate your scripts.


# HTTP Converter vs AI
There are a few differences between the Http Converter and using AI to create your scripts that you might want to consider. Overall, the HTTP Converter is always reliable, while AI can do a better job but is rather prone to hallucinate, especially with larger inputs. Always use your human brain to validate the results an AI gives you. 

* **Larger Uploads:** When uploading larger files, AIs have the tendency to just brush over the file, only extract the website URL and completely ignore the requests in the file and generate some random requests that look nice but have nothing to do with your website. Therefore converting postman collections or curl commands is more reliable than uploading big HAR files. 

* **Reproducability:** The HTTP Converter always creates the same output for the same inputs. AI can give you different outputs whenever it wants.

* **Parametrization:** Some AIs do a better job by doing parameterization, what currently is not A feature of the HTTP Converter.

* **Limitations:** With Free AIs you often run into limitations like "chat limit reached" or "upload limit reached", with the Http Converter app you can convert limitlessly. 


* **Naming:** Most AIs do a better job naming methods and metrics than the HTTP Converter.

* **Post Processing:** With both AI and the Converter you can do post processing of generated input. However, as you write a script when using the Converter, you have 100% reproducability of the same outcome. This is not guaranteed when using AI.


# Comments about different Free AIs

Here some comments about using different Free AIs chatbots to generate Performator Scripts. These are results from short single tests by a single user, you might have different results.

* **ChatGPT:** 
	- Works out of the box after uploading the AGENT.md file.
	- Converts input into really good code in most cases.
	- Several conversions possible before reaching a daily upload limit.
	
* **Grok:** 
	- Works out of the box after uploading the AGENT.md file.
	- Sometimes generates non compiling code.
	- Does not accept "file.har", it has to be renamed to "file.json" in order for it to accept it on upload.
	
* **Claude:** 
	- Works out of the box after uploading the AGENT.md file.
	- Generates nice code, but takes extremely long to do so.
	- Hitting free message limit after a single convertion of a 2MB HAR file.

* **Github Copilot:** 
	- Files can be dragged and dropped from computer.
	- Generates nice code with "Claude Haiku" model.
	- Ignores larger files without giving an error message.

* **DeepSeek:** 
	- Works out of the box after uploading the AGENT.md file.
	- Sometimes generates code that is rather verbose and not really intelligent, but does a great job fixing it when asked to do so.
	- Can convert Postman Files, Curl commands and smaller HAR files.
	- At least in free version: High tendency to run into "chatlimit reached". Uploading a HAR-File of 2MB will already trigger this message and cannot be converted.

* **Google:** 
	- You have to change from the default "Fast" model to at least the "Thinking" model, else it will not give you proper results.
	- Basically does not work for HAR files. Ignores content of larger uploads and hallucinates random requests. This also happens with "Pro" model.
	
* **Microsoft Copilot:**
	- Is pretty touchy and gives strange complaints about "no citations allowed" when not explicity telling it to "convert the file".
	- High tendency to garrulously justify why it cannot do the job or explaining the output, even after the AGENT.md file instructed it to be concise in it's communication.
	- When uploading an input needs a message "please convert it" or else it complains that it cannot do it because of "internal restrictions" that it is not allowed to explain.
	- Does not accept "file.har", it has to be renamed to "file.json" in order for it to accept it on upload.
	


