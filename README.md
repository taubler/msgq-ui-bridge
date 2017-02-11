msgq-ui-bridge
=========

Starts a Vert.x server that reads from an AMPQ queue and pushes real-time events for viewing in a browser.  

Requirements
--------

* To run locally as a Heroku app, a .env file with AMQP_URL property
* To run in Eclipse, under the Run Configuration's Environment tab, a variable called AMQP_URL 

Start the app
--------

Starts the UI server

```./run.sh```

View events in the UI
--------

In a browser ...

[http://localhost:8080](http://localhost:8080)
