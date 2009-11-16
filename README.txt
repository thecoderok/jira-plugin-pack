This project was created by 
Vitaliy Ganzha
http://bytes.org.ua/products/jira-plugin-pack/

And includes work by
Lon F. Binder - lbinder@folica.com

Changelog:
0.0.1 - Created Project
0.0.2 - Converted to JIRA Maven Plugin project structure
        Added "select user" for watchers (this now requires the jira-watcher-field plugin
        
To use this project you must have Atlassian's Maven installed.

To test, in the root project dir run:
atlas-clean
atlas-compile
atlas-package

Then copy .\target\jira-plugin-pack-x.x.x.jar into your Jira plugin folder .\atlassian-jira\WEB-INF\lib\.



More Info:
http://confluence.atlassian.com/display/DEVNET/Developing+your+Plugin+using+the+Atlassian+Plugin+SDK
http://confluence.atlassian.com/display/DEVNET/How+to+Build+an+Atlassian+Plugin
