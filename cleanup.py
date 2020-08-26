import os
import time

#Stop and remove ALL containers
os.popen("docker stop $(docker ps -a -q)")

#time.sleep(3)
#os.popen("docker rm $(docker ps -a -q)")
