import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class GameWatcher implements Watcher {

	// create static instance for zookeeper class.
	private static ZooKeeper zk;

	// create static instance for ZooKeeperConnection class.
	private static ZkConnect conn;

	// declare zookeeper instance to access ZooKeeper ensemble
	private ZooKeeper zoo;
	// final static CountDownLatch connectedSignal = new CountDownLatch(1);
	static String pathScore = "/ZookeeperParentScore";
	static String pathStatus = "/ZookeeperParentStatus";
	static Stat stat;
	static int n = 0;

	// Method to connect zookeeper ensemble.
	public ZooKeeper connect(String host) throws IOException, InterruptedException {
		CountDownLatch connectedSignal = new CountDownLatch(1);
		zoo = new ZooKeeper(host, 5000, this);
		connectedSignal.await();
		return zoo;
	}

	// Method to disconnect from zookeeper server
	public void close() throws InterruptedException {
		zoo.close();
	}

	public static void create(String path, byte[] data) throws KeeperException, InterruptedException {
		zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	// Method to initialize Parent Score and Parent Status
	public static void init() throws KeeperException, InterruptedException {

		byte[] dataScore = "Zookeeper Parent Dir for Score".getBytes();
		byte[] dataStatus = "Zookeeper Parent Dir for Status".getBytes();

		// Parent Score Node
		stat = zk.exists(pathScore, true);
		if (stat == null) {
			create(pathScore, dataScore);
		}
		// Parent Status Node
		stat = zk.exists(pathStatus, true);
		if (stat == null) {
			create(pathStatus, dataStatus);
		}
	}

	public static void displayScores(List<String> onlineNodes, List<String> Scores) {
		TreeMap<Integer, String> tmapScorewise = new TreeMap<Integer, String>(Collections.reverseOrder());
		TreeMap<Integer, String> tmapTimewise = new TreeMap<Integer, String>(Collections.reverseOrder());

		// Recent Scores
		for (String str : Scores) {
			String playerNameScore = str.substring(0, str.lastIndexOf(":"));
			int score = Integer.parseInt(str.substring(str.lastIndexOf(":") + 1));
			tmapTimewise.put(score, playerNameScore);
		}

		System.out.println("Recent scores\n--------------");
		int count = 0;
		for (Map.Entry<Integer, String> entry : tmapTimewise.entrySet()) {
			if (count >= n)
				break;
			String playerNameScore = entry.getValue();
			String playerName = playerNameScore.substring(0, playerNameScore.indexOf(":"));
			int score = Integer.parseInt(playerNameScore.substring(playerNameScore.indexOf(":") + 1));
			System.out.print("\n" + playerName + "\t" + score);
			if (onlineNodes.contains(playerName)) {
				System.out.print(" **");
			}
			count++;
		}

		System.out.println("\n");
		// Highest Scores
		for (String str : Scores) {
			String playerName = str.substring(0, str.indexOf(":"));
			int score = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.lastIndexOf(":")));
			tmapScorewise.put(score, playerName);
		}
		count = 0;
		System.out.println("Highest scores\n--------------");
		for (Map.Entry<Integer, String> entry : tmapScorewise.entrySet()) {
			if (count >= n)
				break;
			String playerName = entry.getValue();
			System.out.print("\n" + playerName + "\t" + entry.getKey());
			if (onlineNodes.contains(playerName)) {
				System.out.print(" **");
			}
			count++;
		}
		System.out.println("\n");
	}
	
	public GameWatcher() throws KeeperException, InterruptedException {
		@SuppressWarnings("unused")
		List<String> temp = zk.getChildren(pathScore, this);
		@SuppressWarnings("unused")
		List<String> temp1 = zk.getChildren(pathStatus, this);
	}

	public static void main(String[] args) {
		String ip = args[0];
		n = Integer.parseInt(args[1]);
		

		try {
			conn = new ZkConnect();
			zk = conn.connect(ip);
			init();// creates parent nodes
			@SuppressWarnings("unused")
			GameWatcher g = new GameWatcher();
			
			while (true) {
				
			}
			// conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage()); // Catch error message
		}
	}

	@Override
	public void process(WatchedEvent we) {
		// TODO Auto-generated method stub

		if (we.getType() == EventType.NodeChildrenChanged) {
//			System.out.println("Watching...");

			try {
				List<String> onlineNodes = zk.getChildren(pathStatus, this);
				List<String> Score = zk.getChildren(pathScore, this);
				displayScores(onlineNodes, Score);
			} catch (KeeperException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
