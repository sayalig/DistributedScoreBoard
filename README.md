# DIC-HW1

To run follow the steps:
1. Copy the 2 JAR files: [Player.jar](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/Player.jar) and [Watcher.jar](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/Watcher.jar) to the destination you want to test run at.
2. Copy the scripts: [player.sh](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/player.sh) and [watcher.sh](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/watcher.sh) in same directory
3. Change mode to executabe for above scripts ``chmod +x player.sh`` and ``chmod +x watcher.sh``
4. Run the scripts (Assuming the zookeeper server is already running) as  
 ```
  "./watcher.sh <ip> <N>"  
  "./player.sh <ip> <player_name>"  
  "./player.sh <ip> <player_name> <count> <delay> <score>"
  ```
5. You can leave the game using ``ctrl+C`` in interactive mode 
