#!/bin/bash
sudo apt-get update
sudo apt-get install default-jdk -y

wget "https://apache.org/dist/zookeeper/zookeeper-3.4.12/zookeeper-3.4.12.tar.gz"
tar -xvzf zookeeper-3.4.12.tar.gz
wget "https://www.slf4j.org/dist/slf4j-1.7.25.tar.gz"
tar -xvzf slf4j-1.7.25.tar.gz

mkdir dependency

cp zookeeper-3.4.12/zookeeper-3.4.12.jar dependency/
cp slf4j-1.7.25/slf4j-api-1.7.25.jar dependency/
cp slf4j-1.7.25/slf4j-simple-1.7.25.jar dependency/

mkdir src
cp Player.java ZkConnect.java GameWatcher.java src/

javac -cp "dependency/*" src/ZkConnect.java src/Player.java src/GameWatcher.java
#java -cp "src/:dependency/*" Player
#java -cp "src/:dependency/*" GameWatcher

