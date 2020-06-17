# COVID-Backend
### Dependencies
* Java 8
* Maven

### Use Instructions:
* Fill in the relevant details for your mysql database (endpoint,username and password) under `src/main/resources/application.properties`
* Run `mvn clean install`
* Run `mvn spring-boot:run -Dspring-boot.run.arguments="SQLKeySet"` to start up the backend using the SQLKeySet backend schema. The backend will then listen on port 8080 for incoming requests 
