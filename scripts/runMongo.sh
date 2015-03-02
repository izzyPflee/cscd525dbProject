#!/bin/bash
# Mongo db benchmark script


##########################
# Hard code queries here #
##########################

query1="query"
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
CURDATE=mongo$(date +%Y-%m-%d:%H:%M:%S)
FILE1=$CURDATE-tc
FILE2=$CURDATE-mc
touch $FILE1
touch $FILE2
echo "Begin collection of comparison query results" >> $FILE1
echo "Begin collection of comparison query results" >> $FILE2

INTER=10000

echo $INTER >> $FILE1
echo $INTER >> $FILE2

echo "\n\n" >> $FILE1
echo "\n\n" >> $FILE2


#################################
# Start up mongo database,      #
# check to make sure its there, #
# not built but there		#
#################################
echo "mongo start"


#########################################################
# write the amount of ram being used to ">> filename-m" #
# Write current time in nanoseconds to ">> filename-t"  #
#########################################################
echo $(date +%s%N) >> $FILE1
echo $(free -t -b | grep Mem: | awk '{print $3}') >> $FILE2


#########################################
# for a fix number of iterations	#
# 1) Run query				#
# 2) append time to >> filename-t	#
# 3) append ram usage to >> filename-m  #
# !!! 2) and 3) are the microbenchmarks #
#########################################
a=0
while [ "$a" -lt 10000 ]    # this is loop1
do
   echo $(date +%s%N) >> $FILE1
   echo $(free -t -b | grep Mem: | awk '{print $3}') >> $FILE2
   a=`expr $a + 1`
done


#######################################
# append time to filename-t           #
# append last ram usage to filename-m #
#######################################
echo $(date +%s%N) >> $FILE1
echo $(free -t -b | grep Mem: | awk '{print $3}') >> $FILE2


##############################################
# write "end of mongo comparision benchmarks #
##############################################
echo "end of mongo comparison benchmarks\n\n" >> $FILE1
echo "end of mongo comparison benchmarks\n\n" >> $FILE2


################
# shut down db #
################
echo "shutting down mongo"


#################################################################
# create new file with data:time in filename and '-ms' or '-tc' #
# write "Begin specific benchmarks to -ms and -tc file"		#
#################################################################
CURDATE1=mongo$(date +%Y-%m-%d:%H:%M:%S)

FILE3=$CURDATE-ts
FILE4=$CURDATE-ms

touch $FILE3
touch $FILE4


############
# start db #
############
echo "start mongo again"

echo "\n\nstart of mongo specific benchmarks\n\n" >> $FILE3
echo "\n\nstart of mongo specific benchmarks\n\n" >> $FILE4


###################################
# append ram usage to -ms file	  #
# append current time to -ts file #
###################################
echo $(date +%s%N) >> $FILE3
echo $(free -t -b | grep Mem: | awk '{print $3}') >> $FILE4


#######################################################
# Run specific query for a fixed number of iterations #
# Do the same thing we did in the previous for loop   #
# 1) - 3)					      #
#######################################################
c=0
while [ "$c" -lt 10000 ]    # this is loop1
do
   	echo $(date +%s%N) >> $FILE3
     	echo $(free -t -b | grep Mem: | awk '{print $3}') >> $FILE4   
	c=`expr $c + 1`
done


##################################
# write final time check to file #
# write final ram check to file  #
# shut down db			 #
##################################
echo $(date +%s%N) >> $FILE3
echo $(free -t -b | grep Mem: | awk '{print $3}') >> $FILE4 


echo "end of mongo specific benchmarks\n\n" >> $FILE3
echo "end of mongo specific benchmarks\n\n" >> $FILE4


###########################
# apply appropriate flags #
###########################
echo "append flags"
