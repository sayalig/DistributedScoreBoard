import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Player {
	final static double sd_score = 9.99, sd_delay = 1.5;

	// create static instance for zookeeper class.
	private static ZooKeeper zk;

	// create static instance for ZooKeeperConnection class.
	private static ZkConnect conn;

	// declare zookeeper instance to access ZooKeeper ensemble
	private ZooKeeper zoo;
	final CountDownLatch connectedSignal = new CountDownLatch(1);
	static Stat stat;
	static String pathScore = "/ZookeeperParentScore";
	static String pathStatus = "/ZookeeperParentStatus";
	static byte[] dataNode = "Zookeeper Dir for Player".getBytes();

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

	// Method to create parent nodes
	public static void create(String path, byte[] data) throws KeeperException, InterruptedException {
		zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	public static void createPlayerNodes(String path, byte[] data) throws KeeperException, InterruptedException {
		zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
	}

	// Method to initialize Parent Score and Parent Status
	public static void init() throws KeeperException, InterruptedException {

		byte[] dataScore = "Zookeeper Parent Dir for Score".getBytes();
		// byte[] dataStatus = "Zookeeper Parent Dir for Status".getBytes();

		// Parent Score Node
		stat = zk.exists(pathScore, true);
		if (stat == null) {
			create(pathScore, dataScore);
		}
		// Parent Status Node
		stat = zk.exists(pathStatus, true);
		if (stat == null) {
			create(pathStatus, dataScore);
		}
	}

	public static void playerOnline(String name) throws KeeperException, InterruptedException {
		String nodeName = pathStatus + "/" + name;
		stat = zk.exists(nodeName, true);
		if (stat != null) {
			System.out.println("Node already online. Terminating player...");
			System.exit(0);
		} else {
			// System.out.println("Creating Node "+nodeName);
			create(nodeName, dataNode);
		}
	}

	public static void playerOffline(String name) throws InterruptedException, KeeperException {
		String nodeName = pathStatus + "/" + name;
		zk.delete(nodeName, zk.exists(nodeName, true).getVersion());
		// System.out.println(name + " deleted");
	}

	public static void main(String[] args) {
		String ip = args[0];
		String name = args[1];
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					playerOffline(name);
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		try {
			conn = new ZkConnect();
			zk = conn.connect(ip);// switch to IP
			init();
			playerOnline(name);

			/*
			 * Interactive Mode
			 */
			if (args.length < 3) {
				System.out.println("Interactive Mode:");
				Scanner sc = new Scanner(System.in);
				while (sc.hasNext()) {
					String currScore = sc.nextLine();
					try {
						int x = Integer.parseInt(currScore);
						if (x < 0) {
							System.out.println("Score can't be negative.");
						}
					} catch (NumberFormatException e) {
						System.out.println("Score is out of range");
					}
					
					String nodeName = pathScore + "/" + name + ":" + currScore + ":";
					createPlayerNodes(nodeName, currScore.getBytes());
					System.out.println(nodeName);
				}
				sc.close();
			}
			/*
			 * Automated Mode
			 */
			else {
				int count = Integer.parseInt(args[2]);
				int delay = Integer.parseInt(args[3]);
				int score = Integer.parseInt(args[4]);
				if (count < 1) {
					System.out.println("Invalid count value. Try again....");
				} else if (score < 1) {
					System.out.println("Invalid score value. Try again....");
				} else if (delay < 1) {
					System.out.println("Invalid delay value. Try again....");
				} else {
					// System.out.println("count = " + count + " " + "delay = " + delay + " " + "
					// score = " + score);
					Random r = new Random();
					for (int n = 0; n < count; n++) {
						int currScoreNum = (int) (r.nextGaussian() * sd_score + score);
						if (currScoreNum < 0)
							currScoreNum *= (-1);
						String currScore = Integer.toString(currScoreNum);
						String nodeName = pathScore + "/" + name + ":" + currScore + ":";
						createPlayerNodes(nodeName, currScore.getBytes());
						long del = (long) (r.nextGaussian() * sd_delay + delay);
						// System.out.println(nodeName+ " " + del);
						TimeUnit.SECONDS.sleep(del);
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage()); // Catch error message
		}

	}// end of main

}// end of class
