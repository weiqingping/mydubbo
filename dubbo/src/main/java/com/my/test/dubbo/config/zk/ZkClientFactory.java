package com.my.test.dubbo.config.zk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.my.test.dubbo.config.registers.ZkCallBack;
import com.my.test.dubbo.config.util.URL;

public class ZkClientFactory {
	private String zkAddress;
	CuratorFramework client;
	public final static String PROVIDER_PATH = "/PROVIDER";
	public final static String DUBBO_PATH = "/DUBBO";
	private static ZkClientFactory instance;

	public static synchronized ZkClientFactory instance(String zkAddress) {
		if (null == instance) {
			instance = new ZkClientFactory(zkAddress);
		}
		return instance;
	}

	public ZkClientFactory(String zkAddress) {
		super();
		if (zkAddress.indexOf("://") > -1) {
			URL url = URL.valueOf(zkAddress);
			zkAddress = URL.valueOf(zkAddress).getHost() + ":" + url.getPort();
		}
		this.zkAddress = zkAddress;
		client = CuratorFrameworkFactory.newClient(zkAddress, new RetryNTimes(10, 5000));
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void close(){
		client.close();
	}
	public String createEphemeral(String path, String data, boolean needClose) throws Exception {
		try {
			return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,
					data.getBytes());

		} finally {
			if (needClose) {
				client.close();
			}
		}
	}

	public boolean isNodeExsits(String path) throws Exception {
		Stat stat = client.checkExists().forPath(path);
		if (null != stat)
			return true;
		return false;
	}

	public String createPersistent(String path, String data, boolean needClose) throws Exception {
		try {
			return client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,
					data.getBytes());

		} finally {
			if (needClose) {
				client.close();
			}
		}
	}

	public Stat set(String path, String data, boolean needClose) throws Exception {
		try {
			return client.setData().forPath(path, data.getBytes());

		} finally {
			if (needClose) {
				client.close();
			}

		}
	}

	public void delete(String path, boolean needClose) throws Exception {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(path);

		} finally {
			if (needClose) {
				client.close();
			}
		}
	}

	public List<String> listChildren(String path, boolean needClose) throws Exception {
		try {
			return client.getChildren().forPath(path);

		} finally {
			if (needClose) {
				client.close();
			}
		}
	}

	public void addListenter(final String className, final ZkCallBack callBack) throws Exception {
		final String path = ZkClientFactory.DUBBO_PATH + "/" + className + PROVIDER_PATH;
		if (!isNodeExsits(path)) {
			try {
				createPersistent(path, " ", false);
			} catch (NodeExistsException e) {
               
			}
		}
		TreeCache treeCache = new TreeCache(client, path);
		// 设置监听器和处理过程
		treeCache.getListenable().addListener(new TreeCacheListener() {

			public void childEvent(CuratorFramework arg0, TreeCacheEvent event) throws Exception {
				// TODO Auto-generated method stub
				ChildData data = event.getData();
				if (data != null) {
					switch (event.getType()) {
					case NODE_ADDED:
						callBack.toDo(className, path, data,event.getType());
						break;
					case NODE_REMOVED:
						callBack.toDo(className, path, data,event.getType());
						break;
					case NODE_UPDATED:
						callBack.toDo(className, path, data,event.getType());
						break;

					default:
						break;
					}
				} else {
					System.out.println("data is null : " + event.getType());
				}
			}
		});
		// 开始监听
		treeCache.start();

	}

}
