#included psql -h localhost -p and specified port number and database name to help compile correctly
#Michael He, 862151198
#Connie Pak, 862128598


#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -h localhost -p 1024 cpak014_DB < $DIR/../src/create_tables.sql
psql -h localhost -p 1024 cpak014_DB < $DIR/../src/create_index.sql
psql -h localhost -p 1024 cpak014_DB < $DIR/../src/load_data.sql
psql -h localhost -p 1024 cpak014_DB < $DIR/../src/triggers.sql