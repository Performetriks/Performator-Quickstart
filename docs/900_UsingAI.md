
# Using AI
As an alternative to the HTTP Converter, you can use AI chatbots like ChatGPT, Copilot or Grok to help you to generate Performator usecase classes from HAR files, Postman collections, curl commands and such.

To train the AI of your choice, we created the following file:

[AGENT.md](./AGENT.md)

Upload the file in your AI chat and add a prompt like:

```
I want you to be the agent explained in the file.
```

Your AI should now be able to receive your input and help you generate your scripts.

# Comments about different AIs

Here some comments about using different Free AIs chatbots to generate Performator Scripts:

* **ChatGPT:** 
	- Works out of the box after uploading the AGENT.md file.
	- Converts input int really good code in most cases.
	- Several conversions possible before reaching a daily upload limit.
	
* **Grok:** 
	- Works out of the box after uploading the AGENT.md file.
	- Sometimes generates non compiling code.
	- Does not accept "file.har", it has to be renamed to "file.json" in order for it to accept it on upload.

* **Claude:** 
	- Works out of the box after uploading the AGENT.md file.
	- Generates nice code, but takes extremely long to do so.
	- Hitting free message limit after a single convertion of a 2MB HAR file.

* **DeepSeek:** 
	- Works out of the box after uploading the AGENT.md file.
	- Sometimes generates code that is rather verbose and not really intelligent, but does a great job fixing it.
	- Can convert Postman Files, Curl commands and smaller HAR files.
	- At least in free version: High tendency to run into "chatlimit reached". Uploading a HAR-File of 2MB will already trigger this message and cannot be converted.
	
* **Copilot:**
	- Is pretty touchy and gives strange complains about "no citations allowed" when not explicity telling it to "convert the file".
	- High tendency to garrulously justify why it cannot do the job or explaining the output, even after the AGENT.md file instructed it to be concise in it's communication.
	- When uploading an input needs a message "please convert it" or else it complains that it cannot do it because of "internal restrictions" that it is not allowed to explain.
	- Does not accept "file.har", it has to be renamed to "file.json" in order for it to accept it on upload.
	


