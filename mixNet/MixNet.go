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
	"strconv"
	"strings"
	"sync"
	"time"
)

const clientPort = 8081
const nodePort = 8082

const msgDelim = "+"

const criticalMass = 1 //The critical mass of messages needed at a node before it forwards the messages onward (Each message contains 14 keys so this is 140 keys)
const queryTime = 5    // How many seconds the system should wait before checking if it has achieved critical mass. Needs to acquire a lock on the dataSet map so shouldn't be too frequent

const (
	nodeConnection   = true
	clientConnection = false
)

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
		log.Fatal("Please make sure your input is of the form \"{NeighborIP} {BackendIP}\"")
	}

	//Generate public and private keys
	privateKey, err := rsa.GenerateKey(rand.Reader, 2048)
	errorCheck(err, "Failed to generate RSA Key", true)

	node := Node{
		apiEndpoint:    "http://" + os.Args[2] + ":8080/InfectedKey",
		neighborNodeIP: os.Args[1] + ":" + strconv.Itoa(nodePort),
		privKey:        privateKey,
		pubKey:         privateKey.PublicKey,
		dataSet:        make(map[string]string),
		mux:            &sync.Mutex{},
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
func (node *Node) criticalMassDetect(wg *sync.WaitGroup) {
	defer wg.Done()

	for {
		fmt.Println(len(node.dataSet))
		node.mux.Lock()
		//If you've reached critical mass then send all of the data you have in a random order to the backend
		if len(node.dataSet) >= criticalMass {
			fmt.Println("Forwarding messages to next node")
			//The order of iterations over maps in go is RANDOM but not every element is equally likely
			//TODO: Make this more random than it currently is
			for data, address := range node.dataSet {
				//Post request if talking to backend, standard TCP message otherwise
				if address == node.apiEndpoint {
					resp, err := http.Post(address, "application/json", bytes.NewBuffer([]byte(data)))
					errorCheck(err, "unable to write to backend", false)
					fmt.Println("Backend Response: " + resp.Status)
				} else {
					outgoingConn, err := net.Dial("tcp", node.neighborNodeIP)
					errorCheck(err, "Failed to connect to neighbor node", false)

					_, err = outgoingConn.Write([]byte(data))
					errorCheck(err, "Failed to write to neighbor node", true)

					err = outgoingConn.Close()
					errorCheck(err, "Failed to close outgoing connection after dialing next node", true)
				}

			}
			node.dataSet = make(map[string]string)
		}

		node.mux.Unlock()

		time.Sleep(queryTime * time.Second)
	}
}

/**
 * Listen for incoming keys from your neighbor node
 */
func (node *Node) nodeConnListen(wg *sync.WaitGroup) {
	defer wg.Done()

	ln, err := net.Listen("tcp", "localhost:"+strconv.Itoa(nodePort))
	errorCheck(err, "Error accepting traffic from neighbor node", false)
	fmt.Println("Passed listen for connections in node listen")

	for {
		conn, err := ln.Accept()
		fmt.Println("Passed accept for connections in node listen")
		errorCheck(err, "Error accepting neighbor node connection", false)
		go node.handleConnecion(conn, nodeConnection)
	}

}

/**
 * Listen for a client who wants to upload keys
 */
func (node *Node) clientConnListen(wg *sync.WaitGroup) {
	defer wg.Done()

	ln, err := net.Listen("tcp", "localhost:"+strconv.Itoa(clientPort))
	errorCheck(err, "Error accepting new client", false)
	fmt.Println("Passed listen for connections")

	for {
		conn, err := ln.Accept()
		fmt.Println("Accepted new connection")
		errorCheck(err, "Error accepting new client", false)
		go node.handleConnecion(conn, clientConnection)
	}
}

/////////////////////////
// Connection Handling //
/////////////////////////

func (node *Node) handleConnecion(incomingConn net.Conn, connectionType bool) {
	fmt.Println("HANDLING CONNECTION")
	message := readSocketData(incomingConn)
	beg := strings.Index(message, msgDelim)
	end := strings.LastIndex(message, msgDelim)
	//message = node.decryptMessage(message[beg+len(msgStart) : end])
	message = message[beg+len(msgDelim) : end]

	fmt.Println("Received Message: " + message)

	node.mux.Lock()
	if connectionType == nodeConnection {
		node.dataSet[message] = node.apiEndpoint
	} else if connectionType == clientConnection {
		node.dataSet[message] = node.neighborNodeIP
	}
	node.mux.Unlock()
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
			if strings.HasSuffix(data, msgDelim) {
				break ReadData
			}
		default:
			log.Fatalf("Receive data failed:%s", err)
			return returnString
		}
	}

	return returnString
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
