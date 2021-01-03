## Build/Install Instructions
#### What is contained in this repository?
 * A Java Spring Boot Backend API
 * Go code for mix network nodes (For upload anonymization)
 * Dockerfiles for the above componenets
 * Python scripts for spinning up and connecting the following containers (2 Mix Nodes -> Backend -> MySQL Database)
 * A basic client that encrypts some data and sends it to the backend through the mix nodes

#### Backend Dependencies
 * Java 8
 * Maven (Will install the remaining dependencies)

#### Mix Network Dependencies
 * Golang 1.14
 * gorilla/mux
 	* Run the following with go installed `go get -u github.com/gorilla/mux`

#### Docker Deployment Dependencies
 * Docker
 * Python3 (If you would like to use the deploy script provided)


#### Building Docker Images
 * Docker images are not yet hosted anywhere so will need be built manually for the time being
 * Run the following commands from the project root directory
 * Building Backend
 	* `mvn clean install`
 	* `mvn clean package`
	* `docker build -t "covid-backend" .
 * Building MixNode
 	* `docker build -t "mix-net-node" ./mixNet
 * Run `docker images` and look for "covid-backend" and "mix-net-node" to make sure that the images have built

#### Deploying the System with Docker
 * Run `python3 run.py` to deploy and connect the backend components
 * You will be asked to select either SQLKeySet or SQLBloomFilter (These can also be passed as an argument to run.py)
 	* See Backend Run Modes below for more details on this
 * The test client in mixnet/TestClient.go can be run with  `go run TestClient.go {MixNode0_IP:port} {MixNode1_IP:port}`. The mix node addresses set by the run.py script are localhost:8081 and localhost:8082

## Backend
### Backend Run Modes
 * The backend has 2 different key storage schemas that result in different behavior
#### SQLKeySet
 * Key Uploads: Keys are stored in plain text as they are collected in a SQL Database
 * Key Requests: Keys are returned to the user in plain text
#### SQLBloomFilter
 * Key Uploads: Keys are stored in a [Bloom Filter](https://en.wikipedia.org/wiki/Bloom_filter) and the original key is discarded
 * Key Requests: A bloom filter with the keys that have been uploaded is returned. The user can then perform their own checks against the bloom filter to check if the keys they have are *possibly* in the bloom filter
 	* Because this approach uses a bloom filter, there is a false positive rate determined by the size of the filter, number of hash functions used, and the number of entries in the filter. The current values for these parameters are meant only for testing.

### Notes
 * Keys that are from after the currentDay of the backend or are from more than 14 days before current day will be discarded when uploaded to prevent unnecessary transactions.
 * Current day updates every 24 hours and starts at 0 when the system starts.
### Making requests to the backend
 * POST /InfectedKey
	 * Allows the user to post a list of json objects containing a "chirp":string and a "day":int to the backend
	 * This endpoint should only be used by the MixNetwork if user upload anonymity is desired
	 * Keys that are not within a 14 day range prior to the current day of the system will be discarded
 	 * Ex: Posting 2 keys from day 1 and day 2 `
	  curl --location --request POST 'localhost:8080/InfectedKey' --header 'Content-Type: application/json' --data-raw '[
			{
				"chirp":"12345678-1234-5678-1234-567812345678",
				"day":1
			},
			{
				"chirp":"32645679-1634-2678-1274-562812345678",
				"day":2
			}
	]'
	`
 * GET /ContactCheck
 	* Requests the keys for a particular day (or some representation of those keys; see backend run modes above)
 	* The query param `day=int` can also be set to only query for keys tagged with that day.
	* `day` can be omitted or set to -1 to request all of the days stored in the system
  	* Ex: A request for all keys from day 2 `curl --location --request GET 'localhost:8080/ContactCheck?day=2' --data-raw ''`

 * POST /ContactCheck
 	* A serverside check for key matches
	* A user can upload a list of json objects containing a "chirp":string and "day":int to be checked against the backend
	* The backend responds with true if a match is found or false if no match is found.
	* May be useful for wearables where resources (memory/compute) may be constrained
 	* Ex: Checking if a chirp is found on the backend`curl --location --request POST 'localhost:8080/ContactCheck' --header 'Content-Type: application/json' --data-raw '{
		"keyArray":[
			{
			"chirp":"12346678-1233-5648-1234-56781234e678",
			"day":1
			}
		]
	}'`

### Posting Keys using the Mix Network
 * A simple example client can be found at mixNet/TestClient.go
 * The goal of the mix network is to make sure that no single server knows both where the keys came from and the actual value of the keys at the same time, therefore the users ip is not linked to the keys that they have uploaded
 * Each node has the following routes
 	* /ClientUpload - This is where the client should upload their encrypted keys
	* /NodeUpload - This is used for communication from the nodes neighbor. Should not be used by the client
	* /PubKey - Used to get the nodes public key to be used for encrypting messages
 * The client should first get the PubKey of both nodes in the network
 * When it sends a message it should then randomly choose one of the nodes to upload to and encrypt the message with the public key of the node it is NOT uploading to. (Ideally we want to encrypt with both nodes keys but the current OAEP RSA encryption does not allow this)
 * Encryption should be RSA OAEP with sha256 hashing
 * This encrypted message should then be the body of a POST request to /ClientUpload. It will then be forwarded, decrypted, then posted to the backend
