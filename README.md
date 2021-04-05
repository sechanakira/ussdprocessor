# ussd-backend
Mama Money USSD Backend Application

Requirements:

JDK 11
Maven
MySQL

The application relies on a MySQL database for persistence.

The application runs on port 8090.

There are 2 ways to run the application:
1. Using Docker.
2. As a standalone application.

Using Docker:
1. Run mvn clean install -DskipTests
2. Run docker-compose up --build -d

As a standalone application:
1. Configure the database configurations in application.properties
2. Build the application with mvn clean install -DskipTests
3. Run java -jar /target/ussdprocessor-0.0.1-SNAPSHOT.jar

Running tests:
mvn clean install

Assumptions made:
1. USSD sessions don't last long. Hanging sessions must be cleared every 5 mins.
2. The data collected is treated as session data and is discarded at the end of the USSD session.