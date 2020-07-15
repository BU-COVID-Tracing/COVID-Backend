# COVID-Backend
### Raw Build Dependencies
* Java 8
* Maven
### Docker Build Dependencies
* Docker
* Python3

### Raw Build Instructions:
* Fill in the relevant details for your mysql database (endpoint,username and password) under `src/main/resources/application.properties`
* The first time you use a database with the application, you will need to set the following in application.properties to create the relevant table `spring.jpa.hibernate.ddl-auto=create`. After the table has been created this parameter can be changed to `none`
* Run `mvn clean install`
* Run `mvn spring-boot:run -Dspring-boot.run.arguments="SQLKeySet"` to start up the backend using the SQLKeySet backend schema. The backend will then listen on port 8080 for incoming requests 

### Sample Requests (For SQLKeySet)

* Post a new key to the database with curl
`curl --location --request POST 'localhost:8080/InfectedKey' \
--header 'Content-Type: application/json' \
--data-raw '[{
	"chirp":"my-key",
	"time":"time"
}]'`

* Get a list of keys in the database with curl 
`curl --location --request GET 'localhost:8080/contactCheck' \
--data-raw ''`

### Building The Docker Image (This image will be hosted somewhere soon)
* With the proper dependencies installed, run mvn clean package. This will create a jar file in the target folder
* Run `docker build -t "covid-backend:OCDockerfile" .` to create the docker image

### Running From Docker
* With the Docker Daemon running, run `python3 run.py`
* If you didn't pass an arg to run.py you will be prompted to pick a run mode
* The script will spin up a container running the backend as well as one running the database required for that schema
* You should then be able to make requests to the backend at localhost:8080
* To shut down the backend, use docker ps to find the id of the container running covid-backend and covid-registry and run docker stop {container-id}
