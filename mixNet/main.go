package main

import (
	"bytes"
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	_ "crypto/sha256"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"sync"
	"time"

	"github.com/gorilla/mux"
)

const LISTEN_PORT = "8081"

const criticalMass = 1 //The critical mass of messages needed at a node before it forwards the messages onward
const queryTime = 5    // How many seconds the system should wait before checking if it has achieved critical mass. Needs to acquire a lock on the dataSet map so shouldn't be too frequent

type Node struct {
	apiEndpoint    string
	neighborNodeIP string
	privKey        *rsa.PrivateKey
	pubKey         rsa.PublicKey

	//In a mix net with more than 2 nodes, the value in this map should be the address that the data needs to be sent to
	dataSet map[string]string //This data structure acts like an unsorted set of data to be sent

	mux *sync.Mutex
}

func main() {
	fmt.Println("Node Started!")

	//TODO: Should do some more thorough checks for proper formatting of args
	if len(os.Args) != 3 {
		log.Fatal("Please make sure your input is of the form \"{Neighbor_IP:Port} {Backend_IP:Port}\"")
	}

	//Generate public and private keys
	privateKey, err := rsa.GenerateKey(rand.Reader, 4096)
	errorCheck(err, "Failed to generate RSA Key", true)

	node := Node{
		apiEndpoint:    "http://" + os.Args[2] + "/InfectedKey",
		neighborNodeIP: "http://" + os.Args[1] + "/NodeTransfer",
		privKey:        privateKey,
		pubKey:         privateKey.PublicKey,
		dataSet:        make(map[string]string),
		mux:            &sync.Mutex{},
	}

	fmt.Println("PublicKey: ", node.pubKey)

	//Start a goroutines listening for clients/nodes

	go node.criticalMassDetect()
	node.handleRequests()
	fmt.Println("Terminated")
}

//////////////////
// Cryptography //
//////////////////

func (node *Node) decryptMessage(encryptedString string) string {
	decryptedBytes, err := node.privKey.Decrypt(nil, []byte(encryptedString), &rsa.OAEPOptions{Hash: crypto.SHA256})
	errorCheck(err, "Failed to decrypt string", false)
	return string(decryptedBytes)
}

////////////////////////////
// Main Execution Threads //
////////////////////////////

/**
 * A goroutine that checks every queryTime seconds to see if this node has hit a critical mass of messages which then means
 * that all of the messages held by this node will be forwarded onward
 * TODO: May be better to forward some percentage of the messages so that it becomes more difficult to trace a particular message
 *       to a particular batch of messages. Also probably a good idea to add a timeout so that messages do not get stuck if the buffer
 *		 does not fill after some period of time
 */
func (node *Node) criticalMassDetect() {
	for {
		node.mux.Lock()
		//If you've reached critical mass then send all of the data you have in a random order to the backend
		if len(node.dataSet) >= criticalMass {
			//The order of iterations over maps in go is RANDOM but not every element is equally likely apparently
			//TODO: Make this more random than it currently is
			for data, address := range node.dataSet {
				//Post request to relevant address
				_, err := http.Post(address, "application/json", bytes.NewBuffer([]byte(data)))
				errorCheck(err, "unable to forward message", false)
			}
			node.dataSet = make(map[string]string)
		}
		node.mux.Unlock()

		time.Sleep(queryTime * time.Second)
	}
}

func (node *Node) handleRequests() {
	myRouter := mux.NewRouter().StrictSlash(true)

	myRouter.HandleFunc("/ClientUpload", node.clientUpload)
	myRouter.HandleFunc("/NodeTransfer", node.nodeUpload)
	myRouter.HandleFunc("/PubKey", node.getPubKey)

	log.Fatal(http.ListenAndServe(":"+LISTEN_PORT, myRouter))
}

func (node *Node) clientUpload(w http.ResponseWriter, r *http.Request) {
	data, err := ioutil.ReadAll(r.Body)
	errorCheck(err, "Failed to read client data", false)

	//RSA padding and message size limits is preventing encrypting messages twice.
	//This can be fixed but will need to find a library that works for go and react-native (So it can be run on the app)
	//Look into libsodium and google/tink
	//decryptedMessage := node.decryptMessage(string(data))

	decryptedMessage := string(data)
	fmt.Println("Client Data: " + decryptedMessage)

	node.mux.Lock()
	node.dataSet[decryptedMessage] = node.neighborNodeIP
	node.mux.Unlock()
}

func (node *Node) nodeUpload(w http.ResponseWriter, r *http.Request) {
	data, err := ioutil.ReadAll(r.Body)
	errorCheck(err, "Failed to read neighbor node data", false)
	decryptedMessage := node.decryptMessage(string(data))
	//decryptedMessage := string(data)

	fmt.Println("Decrypted neighbor data: " + decryptedMessage)
	node.mux.Lock()
	node.dataSet[decryptedMessage] = node.apiEndpoint
	node.mux.Unlock()
}

/**
 * Returns this nodes public key to the requester
 */
func (node *Node) getPubKey(w http.ResponseWriter, r *http.Request) {
	err := json.NewEncoder(w).Encode(node.pubKey)
	errorCheck(err, "Failed to return public key to client", false)
}

/////////////////////////
// Auxiliary Functions //
/////////////////////////

/**
 * Logs a fatal error if err is not nil and the boolean fatal is set
 * @param err The error you would like to check
 * @param msg The message you would like to display if there is an error
 * @param fatal true if an error here should stop the program, false if it should just print the error msg
 */
func errorCheck(err error, msg string, fatal bool) {
	if err != nil {
		fmt.Println(err)
		if fatal {
			log.Fatal(msg)
		} else {
			fmt.Println(msg)
		}
	}
}
