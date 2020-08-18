package main

import (
	"bytes"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"encoding/json"
	"fmt"
	"log"
	"math/big"
	"net/http"
	"os"
)

type client struct {
	node []string
	pubKey[] *rsa.PublicKey

}

//Used for unmarshalling the public key since N is a pointer in rsa.PublicKey
type publicKey struct{
	N big.Int
	E int
}

//A test client that interfaces with the backend
func main() {
	//TODO: Should do some more thorough checks for proper formatting of args
	if len(os.Args) != 3 {
		log.Fatal("Please make sure your input is of the form \"{MixNode0_IP:Port} {MixNet1_IP:Port}\"")
	}

	cl := client{
		node: []string{os.Args[1],os.Args[2]},
		pubKey: make([]*rsa.PublicKey,2),
	}

	cl.getPubKeys()

	testString := "[{\"chirp\":\"123e4567-e89b-12d5-a456-426614134000\",\"day\":\"1\"},{\"chirp\":\"12346678-1233-5648-1234-56781234e678\",\"day\":\"2\"}]"
	data := cl.encryptData(1,[]byte(testString))

	// TODO: Should be encrypting with both nodes keys but padding and rsa message length limits prevent this from functioning properly
	// TODO: Look into libsodium and google/tink for arbitrary length rsa encryption. This will require a react native encryption library as well so that the app can encrypt the data to be sent properly
	//data := cl.encryptData(1,cl.encryptData(0,[]byte(testString)))

	fmt.Println(string(data))
	_, err := http.Post("http://"+cl.node[0]+"/ClientUpload", "text/plain", bytes.NewBuffer(data))
	errCheck(err,"Error posting data",false)
}


func (cl *client) encryptData(nodeNum int,message []byte) []byte{
	ciphertext, err := rsa.EncryptOAEP(sha256.New(), rand.Reader, cl.pubKey[nodeNum], message,[]byte(""))
	errCheck(err,"Failed to encrypt message",false)

	return ciphertext
}

//Get the public keys of the two mixnet nodes
func (cl *client) getPubKeys(){
	//PubKey 0
	key0 := publicKey{}
	resp, err := http.Get("http://"+os.Args[1]+"/PubKey")
	errCheck(err,"Failed to get public key",true)

	err = json.NewDecoder(resp.Body).Decode(&key0)
	errCheck(err,"Failed to read public key",true)
	cl.pubKey[0] = &rsa.PublicKey{
		N: &key0.N,
		E: key0.E,
	}

	err = resp.Body.Close()
	errCheck(err,"Failed to close public key body reader",true)


	//PubKey 1
	key1 := publicKey{}
	resp, err = http.Get("http://"+os.Args[2]+"/PubKey")
	errCheck(err,"Failed to get public key",true)

	err = json.NewDecoder(resp.Body).Decode(&key1)
	errCheck(err,"Failed to read public key",true)
	cl.pubKey[1] = &rsa.PublicKey{
		N: &key1.N,
		E: key1.E,
	}

	err = resp.Body.Close()
	errCheck(err,"Failed to close public key body reader",true)
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
func errCheck(err error, msg string, fatal bool) {
	if err != nil {
		fmt.Println(err)
		if fatal {
			log.Fatal(msg)
		} else {
			fmt.Println(msg)
		}
	}
}