#!/bin/bash

DIR=`dirname $0`

cd $DIR

java -Djava.util.logging.config.file=logging.properties -ms16m -mx16m -jar discretetime-0.2.jar & 
disown
