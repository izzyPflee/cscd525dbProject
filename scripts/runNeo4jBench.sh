#!/bin/bash
# Neo4j db benchmark script
# Author: Jason Helms, Issac Pleegor

##########################
# Hard code queries here #
##########################

QUERY1="echo \`echo 'MATCH (c:CRIME)-[:COMMITTED]->(n:CASE) WHERE c.CRIME=\"HOMICIDE\" return COUNT(DISTINCT(n));' | neo4j-shell -p /db/neo4j/db/\`"
#query2 = ""
#query3 = ""
#query4 = ""
#query5 = ""
#query6 = ""


##############################################################################
# create a new file with the date:time name with a '-tc' for time:comparison #
# create another new file with a '-mc' for memory:comparison		     #
# write "Begin collection of comparision query results"                      #
# write the number of iterations					     #
# write some empty lines (and/or some line marker *****...)		     #
##############################################################################
CURDATE=neo4j$(date +%Y-%m-%d:%H:%M:%S)

FILE1=$CURDATE-tc
FILE2=$CURDATE-mc

touch $FILE1
touch $FILE2
echo `mv $FILE1 benchmarks/`
echo `mv $FILE2 benchmarks/`

echo "Begin collection of comparison query results" >> benchmarks/$FILE1
echo "Begin collection of comparison query results" >> benchmarks/$FILE2

INTER=10000

echo $INTER >> benchmarks/$FILE1
echo $INTER >> benchmarks/$FILE2

echo "\n\n" >> benchmarks/$FILE1
echo "\n\n" >> benchmarks/$FILE2


#################################
# Start up neo4j database,      #
# check to make sure its there, #
# not built but there		#
#################################
#neo4j start
echo `neo4j start`
sleep 5
echo `echo 'MATCH (c:CRIME)-[:COMMITTED]->(n:CASE) WHERE c.CRIME="HOMICIDE" return COUNT(DISTINCT(n));' | neo4j-shell -p /db/neo4j/db/`


#########################################################
# write the amount of ram being used to ">> filename-m" #
# Write current time in nanoseconds to ">> filename-t"  #
#########################################################
echo $(date +%s%N) >> benchmarks/$FILE1
echo $(free -t -b | grep Mem: | awk '{print $3}') >> benchmarks/$FILE2



#########################################
# for a fix number of iterations	#
# 1) Run query				#
# 2) append time to >> filename-t	#
# 3) append ram usage to >> filename-m  #
# !!! 2) and 3) are the microbenchmarks #
#########################################
a=0
while [ "$a" -lt 100 ]    # this is loop1
do
   echo $(date +%s%N) >> benchmarks/$FILE1
   echo $(free -t -b | grep Mem: | awk '{print $3}') >> benchmarks/$FILE2
   a=`expr $a + 1`
done


#######################################
# append time to filename-t           #
# append last ram usage to filename-m #
#######################################
echo $(date +%s%N) >> benchmarks/$FILE1
echo $(free -t -b | grep Mem: | awk '{print $3}') >> benchmarks/$FILE2



##############################################
# write "end of neo4j comparision benchmarks #
##############################################
echo "end of neo4j comparison benchmarks\n\n" >> benchmarks/$FILE1
echo "end of neo4j comparison benchmarks\n\n" >> benchmarks/$FILE2


################
# shut down db #
################
echo "shutting down neo4j"


#################################################################
# create new file with data:time in filename and '-ms' or '-tc' #
# write "Begin specific benchmarks to -ms and -tc file"		#
#################################################################
CURDATE1=neo4j$(date +%Y-%m-%d:%H:%M:%S)

FILE3=$CURDATE-ts
FILE4=$CURDATE-ms

touch $FILE3
touch $FILE4
echo `mv $FILE3 benchmarks/`
echo `mv $FILE4 benchmarks/`

############
# start db #
############
echo "start neo4j again"
echo `neo4j start`

echo "\n\nstart of neo4j specific benchmarks\n\n" >> benchmarks/$FILE3
echo "\n\nstart of neo4j specific benchmarks\n\n" >> benchmarks/$FILE4


###################################
# append ram usage to -ms file	  #
# append current time to -ts file #
###################################
echo $(date +%s%N) >> benchmarks/$FILE3
echo $(free -t -b | grep Mem: | awk '{print $3}') >> benchmarks/$FILE4


#######################################################
# Run specific query for a fixed number of iterations #
# Do the same thing we did in the previous for loop   #
# 1) - 3)					      #
#######################################################
c=0
while [ "$c" -lt 100 ]    # this is loop1
do
   	echo $(date +%s%N) >> benchmarks/$FILE3
     	echo $(free -t -b | grep Mem: | awk '{print $3}') >> benchmarks/$FILE4   
	c=`expr $c + 1`
done


##################################
# write final time check to file #
# write final ram check to file  #
# shut down db			 #
##################################
echo $(date +%s%N) >> benchmarks/$FILE3
echo $(free -t -b | grep Mem: | awk '{print $3}') >> benchmarks/$FILE4 


echo "end of neo4j specific benchmarks\n\n" >> benchmarks/$FILE3
echo "end of neo4j specific benchmarks\n\n" >> benchmarks/$FILE4

echo `neo4j stop`
###########################
# apply appropriate flags #
###########################
echo "append flags"
