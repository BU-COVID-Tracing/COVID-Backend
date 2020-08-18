import os
import time

#Stop and remove all containers
os.popen("docker stop $(docker ps -a -q)")

#time.sleep(3)

#os.popen("docker rm $(docker ps -a -q)")
