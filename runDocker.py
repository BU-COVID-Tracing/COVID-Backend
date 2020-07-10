import os
import sys

optionsList = ["SQLKeySet","SQLBloomFilter"]
argvTried = False #Have you already tried to use argv
selection = ""

while not selection:
    #If an arg has been passed use it. Otherwise prompt the user to pick a run mode
    if len(sys.argv) == 2 and not argvTried:
        print(sys.argv)
        if sys.argv[1] not in optionsList:
            print("Invalid param passed. Please try again")
            argvTried = True
        else:
            selection = sys.argv[1]
    else:
        print("Enter the number next to the schema you would like to run:")
        for ii in range(len(optionsList)):
            print(ii,": ",optionsList[ii])

        try:
            numInput = int(input())
        except ValueError:
            print("Invalid Selection. Try again")
            continue

        if numInput < 0 or numInput >= len(optionsList):
            print("Invalid Selection. Try again")
        else:
            selection = optionsList[numInput]

os.system('docker build --build-arg RUN_MODE=' + selection + ' -t "covid-backend:OCDockerfile" .')

#Need to parse out image id from the previous system call and then run/publish it
#os.system()
