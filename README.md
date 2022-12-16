<h1 align="center">
JSON Database
</h1>
<p align="center">
  <a href="https://github.com/erickfunier">
    <img alt="Made by Erick Santos" src="https://img.shields.io/badge/made%20by-Erick%20Santos-lightgrey">
  </a>
</p>
<p>
    A fully implemented JSON database in Java and stored in a file. Handling nested json values too.br>
    The connection between the client and the server is through a socket, the server is listening the port 23456.
</p>
<h2>Dependencies</h2>
<p>
    JRE 17
</p>

<p>Start the Server:</p>
     
    java Server
	
<p>Start the Client with arguments:</p>
     
	[-in] to use input as file .json 
    java Client -in setFile.json
	
	[-t type] as type of command (set, get, delete, exit)
	[-k key] as key of value
	[-v value] as value of key
	java Client -t set -k 1 -v "Hello world!"

<h2>Environment development</h2>
<p>The application is ready to run in IntelliJ IDE (2022.3), you can import the project from the GitHub repository.</p>

<h2>Execution example from client view</h2>

    > java Main -t get -k 1
    Client started!
    Sent: {"type":"get","key":"1"}
    Received: {"response":"ERROR","reason":"No such key"}

    > java Main -t set -k 1 -v "Hello world!"
    Client started!
    Sent: {"type":"set","key":"1","value":"Hello world!"}
    Received: {"response":"OK"}

    > java Main -in setFile.json
    Client started!
    Sent:
    {
        "type":"set",
        "key":"person",
        "value":{
            "name":"Elon Musk",
            "car":{
                "model":"Tesla Roadster",
                "year":"2018"
            },  
            "rocket":{
                "name":"Falcon 9",
                "launches":"87"
            }
        }   
    }
    Received: {"response":"OK"}

    > java Main -in getFile.json
    Client started!
    Sent: {"type":"get","key":["person","name"]}
    Received: {"response":"OK","value":"Elon Musk"}

    > java Main -in updateFile.json
    Client started!
    Sent: {"type":"set","key":["person","rocket","launches"],"value":"88"}
    Received: {"response":"OK"}