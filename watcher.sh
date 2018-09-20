#!/bin/bash
if  [ "$#" -eq 2 ]; then
     java -jar Watcher.jar $1 $2
else
     echo "Illegal number of parameters"
fi
