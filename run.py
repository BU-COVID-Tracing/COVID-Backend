import os
import sys
import random
import time

optionsList = ["SQLKeySet", "SQLBloomFilter"]
argvTried = False  # Have you already tried to use argv
selection = ""

while not selection:
    # If an arg has been passed use it. Otherwise prompt the user to pick a run mode
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
            print(ii, ": ", optionsList[ii])

        try:
            numInput = int(input())
        except ValueError:
            print("Invalid Selection. Try again")
            continue

        if numInput < 0 or numInput >= len(optionsList):
            print("Invalid Selection. Try again")
        else:
            selection = optionsList[numInput]

# Generate a random password for the db
# This probably isn't secure enough for actual production use but is fine for testing
random.seed(time.time())
time.sleep(random.uniform(0, 1))
random.seed(time.time())
dbPassword = str(random.randint(0, sys.maxsize))
print("Database Password: ",dbPassword)

dbPasswordVar = " --spring.datasource.password=" + dbPassword 
dbUser = " --spring.datasource.username=root"
dbURL = " --spring.datasource.url=jdbc:mysql://covid-registry:3306/registry?createDatabaseIfNotExist=true"


#Create a network bridge 
os.popen("docker network create --driver bridge covid-net")
# Decide which database should be spun up based on the run mode of the backend
if selection is optionsList[0] or optionsList[1]:
    dbContainerID = os.popen("docker run -d -p 3306:3306 --network covid-net --name=covid-registry -e MYSQL_ROOT_PASSWORD=" + dbPassword + " mysql").read()

    # Should try to do a check to see if the db is running rather than just sleeping
    time.sleep(10) # Give time for the database to initialize before launching the backend

    #TODO: Substitute the location of the hosted image once it is up
    os.popen("docker run -p 8080:8080 -dit --name=covid-backend --network covid-net covid-backend:OCDockerfile " + selection + dbPasswordVar + dbUser + dbURL)
