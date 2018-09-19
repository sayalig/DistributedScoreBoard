import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Player {
	// create static instance for zookeeper class.
	private static ZooKeeper zk;

	// create static instance for ZooKeeperConnection class.
	private static ZkConnect conn;

	// declare zookeeper instance to access ZooKeeper ensemble
	private ZooKeeper zoo;
	final CountDownLatch connectedSignal = new CountDownLatch(1);

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

	// Method to create znode in zookeeper ensemble
	public static void create(String path, byte[] data) throws KeeperException, InterruptedException {
		zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "/ZookeeperParent";
		String ip = args[0];
		String name = args[1];
		try {
			conn = new ZkConnect();
			zk = conn.connect("localhost");
			byte[] data = "Zookeeper Parent Dir".getBytes(); // Declare data
			// create(path, data); // Create the data to the specified path

			Stat stat = zk.exists(path, true); // Stat checks the path of the znode

			if (stat != null) {
				System.out.println("Node exists and the node version is " + stat.getVersion());
			} else {
				System.out.println("Node does not exists");
			}

			if (args[2] == null) {// Its player online post
				// Update player status to online
				

			} else {// its a score post

				int count = Integer.parseInt(args[2]);
				int delay = Integer.parseInt(args[2]);
				int score = Integer.parseInt(args[2]);

				int n = 0;
				while (n < count) {
					String node = path + "/" + name + n;

					create(node, data);
					System.out.println(node);
					// delete children
					// zk.delete(node, zk.exists(node, true).getVersion());
					// System.out.println(node + " deleted");
					n++;
				}
			}
			// delete parent node
			// zk.delete(path, zk.exists(path, true).getVersion());
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage()); // Catch error message
		}

	}// end of main

}// end of class
