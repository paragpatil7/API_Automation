FROM openjdk:8u191-jre-alpine3.8



# Workspace
WORKDIR /usr/share/Parag

# ADD .jar under target from host
# into this image
ADD target/APIAutomation.jar 			APIAutomation.jar
ADD target/APIAutomation-tests.jar	 	APIAutomation-tests.jar
ADD target/libs							libs

# in case of any other dependency like .csv / .json / .xls
# please ADD that as well
ADD	target/Expected_Documents_per_category.json		Expected_Documents_per_category.json
# ADD suite files
# ADD extent-config.xml						extent-config.xml



# BROWSER
# HUB_HOST
# MODULE

ENTRYPOINT java -cp APIAutomation.jar:APIAutomation-tests.jar:libs/*  org.junit.runner.JUnitCore com.automation.test.TestRunner.TestRunner