<h1 align="center">
JSON Database
</h1>
<p align="center">
  <a href="https://github.com/erickfunier">
    <img alt="Made by Erick Santos" src="https://img.shields.io/badge/made%20by-Erick%20Santos-lightgrey">
  </a>
</p>
<p>
    A fully implemented JSON database in Java and stored in a file. Handling nested json values too.
</p>
<h2>Dependencies</h2>
<p>
    JRE 17
</p>

<p>Run the Server</p>
     
    java Server
	
<p>Run the Client with arguments</p>
     
	-in to use input as file .json 
    java Client -in setFile.json
	
	-t as type of command (set, get, delete, exit)
	-k as key of value
	-v as value of key
	java Client -t set -k 1 -v "Hello world!"

<h2>Environment development</h2>
<p>The application is ready to run in IntelliJ IDE (2022.3), you can import the project from the GitHub repository</p>
