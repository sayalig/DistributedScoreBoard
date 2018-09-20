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
import org.apache.zookeeper.Watcher.Event.KeeperState;
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
	final CountDownLatch connectedSignal = new CountDownLatch(1);
	static String pathScore = "/ZookeeperParentScore";
	static String pathStatus = "/ZookeeperParentStatus";
	static Stat stat;

	// Method to connect zookeeper ensemble.
	public ZooKeeper connect(String host) throws IOException, InterruptedException {

		zoo = new ZooKeeper(host, 5000, new Watcher() {

			public void process(WatchedEvent we) {

				if (we.getState() == KeeperState.SyncConnected) {
					connectedSignal.countDown();
				}
			}
		});

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

	/*
	 * public void run() { try { synchronized (this) { while (!dm.dead) { wait(); }
	 * } } catch (InterruptedException e) { } }
	 */

	public static void main(String[] args) {
		String ip = args[0];
		int n = Integer.parseInt(args[1]);
		TreeMap<Integer, String> tmap = new TreeMap<Integer, String>(Collections.reverseOrder());
		try {
			conn = new ZkConnect();
			zk = conn.connect(ip);
			init();
			List<String> onlineNodes = zk.getChildren(pathStatus, true);
			List<String> Scores = zk.getChildren(pathScore, true);
			for (String str : Scores) {
				// System.out.println(str);
				String playerName = str.substring(0, str.indexOf(":"));
				int score = Integer.parseInt(str.substring(str.indexOf(":") + 1, str.lastIndexOf(":")));
				tmap.put(score, playerName);
				System.out.print("\n" + playerName + "\t" + score);
				if (onlineNodes.contains(playerName)) {
					System.out.print(" **");
				}
			}
			// Recent Scores
			/*
			 * System.out.println("Recent scores\n--------------"); int count = 0; for
			 * (Map.Entry<Integer, String> entry : tmap.entrySet()) { if (count >= n) break;
			 * String playerName = entry.getValue(); System.out.print("\n" + playerName +
			 * "\t" + entry.getKey()); if (onlineNodes.contains(playerName)) {
			 * System.out.print(" **"); } count++; }
			 */
			System.out.println("\n");
			// Highest Scores
			int count = 0;
			System.out.println("Highest scores\n--------------");
			for (Map.Entry<Integer, String> entry : tmap.entrySet()) {
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
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage()); // Catch error message
		}
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub

	}

}
