package com.my.test.dubbo.config.model;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.springframework.beans.factory.InitializingBean;
import com.my.test.dubbo.config.registers.RegistoryAddressContiner;
import com.my.test.dubbo.config.registers.ZkCallBack;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;
import com.my.test.dubbo.config.zk.ZkClientFactory;

public class RegistryConfig implements InitializingBean, ZkCallBack {
	private String name;
	private String address;
	private volatile RegistoryAddressContiner contailner;
	private ZkClientFactory factory;

	public RegistoryAddressContiner getContailner() {
		return contailner;
	}

	public void setContailner(RegistoryAddressContiner contailner) {
		this.contailner = contailner;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		contailner = new RegistoryAddressContiner();

		factory = new ZkClientFactory(address);

	}

	public synchronized void loadService(String className) throws Exception {
		// ZkClientFactory factory=new ZkClientFactory(address);
		final String providerPath = ZkClientFactory.DUBBO_PATH + "/" + className + ZkClientFactory.PROVIDER_PATH;
		contailner.clearKeyMap(className);
		List<String> children = factory.listChildren(providerPath, false);
		if (null != children && !children.isEmpty()) {
			loadRegistry(className, children, providerPath);

		}
	}

	public synchronized void addListener(String interfaceClass) throws Exception {
		factory.addListenter(interfaceClass, this);

	}

	@Override
	public void toDo(String className, String path, ChildData data,Type opType) throws Exception {
		List<String> children = factory.listChildren(path, false);
		contailner.clearKeyMap(className);
		if (null != children && !children.isEmpty()) {
			loadRegistry(className, children, path);
		}

	}

	private synchronized void loadRegistry(String className, List<String> children, String parentPath) {
		for (String str : children) {
			String urlStr = URLDecoder.decode(str);
			URL url = URL.valueOf(urlStr);
			//String version = StringUtils.isEmpty(url.getParameter("version")) ? "*" : url.getParameter("version");
			contailner.addUrl(className, urlStr);
		}
	}

	public void fulshRemoteConfig() {
		if (StringUtils.isNotEmpty(getAddress())) {
			URL url = URL.valueOf(getAddress());
			if ("zookeeper".equals(url.getProtocol())) {
				/*
				 * ZkClientFactory factory=new
				 * ZkClientFactory(url.getHost()+":"+url.getPort()); factory.de
				 */
				this.factory.close();
			}

		}
	}

}
