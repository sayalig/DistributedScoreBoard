# DIC-HW1

To run follow the steps:
1. Copy the 2 JAR files: [Player.jar](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/Player.jar) and [Watcher.jar](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/Watcher.jar)
2. Copy the scripts: [player.sh](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/player.sh) and [watcher.sh](https://github.ncsu.edu/ssgodbol/DIC-HW1/blob/master/watcher.sh)
3. Change mode to executabe for above scripts
4. Run the scripts (Assuming the zookeeeper server is already running) as  
 ```
  "./watcher.sh <ip> <N>"  
  "./player.sh <ip> <player_name>"  
  "./player.sh <ip> <player_name> <count> <delay> <score>"
  ```
5. You can leave the game using ``ctrl+C`` in interactive mode 
