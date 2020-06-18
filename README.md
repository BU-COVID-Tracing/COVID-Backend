# COVID-Backend
### Dependencies
* Java 8
* Maven

### Use Instructions:
* Fill in the relevant details for your mysql database (endpoint,username and password) under `src/main/resources/application.properties`
* The first time you use a database with the application, you will need to set the following in application.properties to create the relevant table `spring.jpa.hibernate.ddl-auto=create`. After the table has been created this parameter can be changed to `none`
* Run `mvn clean install`
* Run `mvn spring-boot:run -Dspring-boot.run.arguments="SQLKeySet"` to start up the backend using the SQLKeySet backend schema. The backend will then listen on port 8080 for incoming requests 
