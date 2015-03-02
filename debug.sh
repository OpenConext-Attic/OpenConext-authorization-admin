#!/bin/bash
# file for local development to start the application.
mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
