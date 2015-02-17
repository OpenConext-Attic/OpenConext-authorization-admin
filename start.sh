#!/bin/bash
# file for local development to start the application.
mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev"
