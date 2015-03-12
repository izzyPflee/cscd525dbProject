
#! /user/bin/env python
import subprocess

# running 
# mongo project query3.js | grep millis >> results3.txt

#temp

for x in xrange(0,10):
	command = "mongo project query3.js | grep millis >> results3.txt"
	subprocess.call(command, shell=True)


	