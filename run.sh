#!/bin/bash
mvn -q -DskipTests clean package
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/msgq-ui-bridge-1.0-SNAPSHOT-fat.jar
