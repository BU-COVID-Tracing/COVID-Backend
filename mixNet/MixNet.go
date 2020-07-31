package main

import (
	"bufio"
	"bytes"
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	_ "crypto/sha256"
	"fmt"
	"io"
	"log"
	"net"
	"net/http"
	"os"
	"strings"
	"sync"
	"time"
)

const containerName = "MixNet"

const msgStart = "$"
const msgEnd = "$"

const criticalMass = 10 //The critical mass of messages needed at a node before it forwards the messages onward (Each message contains 14 keys so this is 140 keys)

type Node struct {
	backendNodeIP  string
	neighborNodeIP string
	privKey        *rsa.PrivateKey
	pubKey         rsa.PublicKey

	//In a mix net with more than 2 nodes, the value in this map should be the address that the data needs to be sent to
	dataSet map[string]string //This data structure acts like an unsorted set of data to be sent

	mux *sync.Mutex
}

func main() {
	//TODO: Should do some more thorough checks for proper formatting of args
	if len(os.Args) != 3 {
		log.Fatal("Please make sure that you pass 0 or 1 as the first arg and the address of the backend as the second arg")
	}

	//Generate public and private keys
	privateKey, err := rsa.GenerateKey(rand.Reader, 2048)
	errorCheck(err, "Failed to generate RSA Key", true)

	node := Node{
		backendNodeIP:  "http://" + os.Args[2] + "/InfectedKey",
		neighborNodeIP: containerName + os.Args[1] + ":8081", //This is a container name at the moment which should be resolved by docker DNS
		//neighborNodeIP: "localhost:8081",
		privKey: privateKey,
		pubKey:  privateKey.PublicKey,
		dataSet: make(map[string]string),
		mux:     &sync.Mutex{},
	}

	fmt.Println("PublicKey: ", node.pubKey)

	//Start a goroutines listening for clients/nodes

	var wg sync.WaitGroup

	wg.Add(3)
	go node.nodeConnListen(&wg)
	go node.clientConnListen(&wg)
	go node.criticalMassDetect(&wg)

	//Wait for goroutines to finish (They shouldn't so long as the node keeps running)
	wg.Wait()
}

//////////////////
// Cryptography //
//////////////////

func (node *Node) decryptMessage(encryptedString string) string {
	decryptedBytes, err := node.privKey.Decrypt(nil, []byte(encryptedString), &rsa.OAEPOptions{Hash: crypto.SHA256})
	errorCheck(err, "Failed to decrypt string", true)
	return string(decryptedBytes)
}

//////////////////////////////
// Node Connection Handling //
//////////////////////////////

func (node *Node) criticalMassDetect(wg *sync.WaitGroup) {
	defer wg.Done()

	for {
		node.mux.Lock()
		//If you've reached critical mass then send all of the data you have in a random order to the backend
		if len(node.dataSet) == criticalMass {
			fmt.Println("Forwarding messages to next node")
			//The order of iterations over maps in go is RANDOM but not every element is equally likely
			//TODO: Make this more random than it currently is
			for data, address := range node.dataSet {
				//Post request if talking to backend, standard TCP message otherwise
				if address == node.backendNodeIP {
					resp, err := http.Post(address, "application/json", bytes.NewBuffer([]byte(data)))
					errorCheck(err, "unable to write to backend", false)
					fmt.Println("Backend Response: " + resp.Status)
				} else {
					outgoingConn, err := net.Dial("tcp", node.neighborNodeIP)
					errorCheck(err, "Failed to connect to neighbor node", false)

					_, err = outgoingConn.Write([]byte(data))
					errorCheck(err, "Failed to write to neighbor node", true)

					outgoingConn.Close()
				}

			}
		}
		node.dataSet = make(map[string]string)

		node.mux.Unlock()

		time.Sleep(5 * time.Second)
	}
}

/**
 * Listen for incoming keys from your neighbor node
 */
func (node *Node) nodeConnListen(wg *sync.WaitGroup) {
	defer wg.Done()

	ln, err := net.Listen("tcp", "localhost:8081")
	errorCheck(err, "Error accepting traffic from neighbor node", false)

	for {
		conn, err := ln.Accept()
		errorCheck(err, "Error accepting neighbor node connection", false)
		go node.handleNodeConnection(conn)
	}

}

func (node *Node) handleNodeConnection(incomingConn net.Conn) {
	nodeMessage := readSocketData(incomingConn)
	beg := strings.Index(nodeMessage, msgStart)
	end := strings.Index(nodeMessage, msgEnd)
	decryptedString := node.decryptMessage(nodeMessage[beg+len(msgStart) : end])

	fmt.Println("Received Neighbor Message: " + decryptedString)

	node.mux.Lock()
	node.dataSet[decryptedString] = node.backendNodeIP
	node.mux.Unlock()
}

////////////////////////////////
// Client Connection Handling //
////////////////////////////////

/**
 * Listen for a client who wants to upload keys
 */
func (node *Node) clientConnListen(wg *sync.WaitGroup) {
	defer wg.Done()

	ln, err := net.Listen("tcp", "localhost:8082")
	errorCheck(err, "Error accepting new client", false)

	for {
		conn, err := ln.Accept()
		errorCheck(err, "Error accepting new client", false)
		go node.handleClientConnection(conn)
	}
}

func (node *Node) handleClientConnection(incomingConn net.Conn) {
	clientMessage := readSocketData(incomingConn)
	beg := strings.Index(clientMessage, msgStart)
	end := strings.Index(clientMessage, msgEnd)
	clientMessage = node.decryptMessage(clientMessage[beg+len(msgStart) : end])
	fmt.Println("Received Client Message: " + clientMessage)

	node.mux.Lock()
	node.dataSet[clientMessage] = node.neighborNodeIP
	node.mux.Unlock()
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
		if fatal {
			log.Fatal(msg)
		} else {
			fmt.Println(msg)
		}
	}
}

/**
 * TODO: This is a bit messy and is likely breakable
 */
func readSocketData(incomingConn net.Conn) (returnString string) {
	defer incomingConn.Close()

ReadData:
	for {
		buf := make([]byte, 1024)
		length, err := bufio.NewReader(incomingConn).Read(buf)
		data := string(buf[:length])
		returnString += data

		switch err {
		case io.EOF:
			break ReadData
		case nil:
			if strings.HasSuffix(data, "END") {
				break ReadData
			}
		default:
			log.Fatalf("Receive data failed:%s", err)
			return returnString
		}
	}

	return returnString
}
