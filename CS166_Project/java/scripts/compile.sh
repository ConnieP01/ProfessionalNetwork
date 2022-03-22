#Michael He, 862151198
#Connie Pak, 862128598
#modified port number and database name in last command, put export DB_NAME=$USER"_DB" as a command, and added in a classes folder in the java directory to help this file compile correctly.

#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export DB_NAME=$USER"_DB"

# Indicate the path of the java compiler to use
export JAVA_HOME=/usr/csshare/pkgs/jdk1.7.0_17
export PATH=$JAVA_HOME/bin:$PATH

# compile the java program
javac -d $DIR/../classes $DIR/../src/ProfNetwork.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../classes:$DIR/../lib/pg73jdbc3.jar ProfNetwork $DB_NAME 1024 cpak014
