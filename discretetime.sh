#!/bin/bash

DIR=`dirname $0`

cd $DIR

java -Dperiod=600000 -Djava.util.logging.config.file=logging.properties -ms16m -mx16m -jar discretetime-0.5.jar & 
disown
