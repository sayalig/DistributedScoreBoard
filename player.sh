#!/bin/bash
if  [ "$#" -eq 2 ]; then
     java -jar Player.jar $1 $2
elif  [ "$#" -eq 5 ]; then
     java -jar Player.jar $1 $2 $3 $4 $5
else
     echo "Illegal number of parameters"
fi
