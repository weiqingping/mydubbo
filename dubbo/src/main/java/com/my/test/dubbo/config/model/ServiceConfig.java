package com.my.test.dubbo.config.model;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.my.test.dubbo.config.protocol.ProtocolExport;
import com.my.test.dubbo.config.protocol.ProtocolFactory;
import com.my.test.dubbo.config.protocol.ServerHolder;
import com.my.test.dubbo.config.server.Server;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.NetUtils;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;
import com.my.test.dubbo.config.zk.ZkClientFactory;
import com.my.test.dubbo.container.ConfigUtil;

public class ServiceConfig<T> extends AbstractInterfaceConfig
		implements InitializingBean, BeanFactoryAware, DisposableBean {
	private static final Map<String, Integer> RANDOM_PORT_MAP = new HashMap<String, Integer>();
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServiceConfig.class);
	private String className;
	private BeanFactory beanFactory;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getLoadBanlance() {
		return loadBanlance;
	}

	public void setLoadBanlance(String loadBanlance) {
		this.loadBanlance = loadBanlance;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}

	private String loadBanlance;
	private int weight;
	private T ref;
	private List<ProtocolConfig> protocols;

	public List<ProtocolConfig> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<ProtocolConfig> protocols) {
		this.protocols = protocols;
	}

	public void exportServiceUrls(ProtocolConfig protocol) throws Exception {

		ProtocolExport export = ProtocolFactory.get(protocol);
		if (null == export)
			throw new IllegalArgumentException("the protocol Argument is vaild");
		String name = protocol.getName();
		if (name == null || name.length() == 0) {
			name = Constants.DEFAULT_PROTOCOL;
		}

		String host = protocol.getHost();
		boolean anyhost = false;
		if (NetUtils.isInvalidLocalHost(host)) {
			anyhost = true;
			try {
				host = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				logger.warn(e.getMessage(), e);
			}
			if (NetUtils.isInvalidLocalHost(host)) {
				if (this.getRegistrys() != null && !this.getRegistrys().isEmpty()) {
					try {
						Socket socket = new Socket();
						try {
							URL url = URL.valueOf(getRegistrys().get(0).getAddress());
							SocketAddress addr = new InetSocketAddress(url.getHost(), url.getPort());
							socket.connect(addr, 1000);
							host = socket.getLocalAddress().getHostAddress();
						} finally {
							try {
								socket.close();
							} catch (Throwable e) {
							}
						}
					} catch (Exception e) {
						logger.warn(e.getMessage(), e);
					}

				}
				if (NetUtils.isInvalidLocalHost(host)) {
					host = NetUtils.getLocalHost();
				}
			}
		}
		protocol.setHost(host);

		Integer port = protocol.getPort();

		final String defaultPort = ConfigUtil.getValue("dubbo.protocl.port");
		if (port == null || port == 0) {
			port = Integer.parseInt(defaultPort);
		}
		if (port == null || port <= 0) {
			port = getRandomPort(name);
			if (port == null || port < 0) {
				port = NetUtils.getAvailablePort(Integer.parseInt(defaultPort));
				putRandomPort(name, port);
			}
			logger.warn("Use random available port(" + port + ") for protocol " + name);
		}

		protocol.setPort(port);
		registService(protocol);
		export.export(this, protocol);

	}

	public void afterPropertiesSet() throws Exception {
		if (null != this.getProtocols() && !this.getProtocols().isEmpty()) {
			for (ProtocolConfig protocol : this.getProtocols()) {
				this.exportServiceUrls(protocol);
			}
		}

	}

	public void registService(ProtocolConfig protocol) throws Exception {
		final String parentPath = ZkClientFactory.DUBBO_PATH + "/" + this.getInterfaces()
				+ ZkClientFactory.PROVIDER_PATH;
		String version = this.getVersion();
		String loadBanlance = this.getLoadBanlance();
		String serriza = this.getSerialize();
		long timeOut = this.getTimeout();
		if (StringUtils.isEmpty(version)) {
			version = "*";
		}
		if (StringUtils.isEmpty(loadBanlance)) {
			loadBanlance = protocol.getLoadBanlance();
		}

		if (StringUtils.isEmpty(loadBanlance)) {
			loadBanlance = Constants.DEFAULT_LOADBALANCE;
		}

		if (StringUtils.isEmpty(serriza)) {
			serriza = protocol.getSerialize();
		}

		if (StringUtils.isEmpty(serriza)) {
			serriza = Constants.DEFAULT_REMOTING_SERIALIZATION;
		}

		if (timeOut <= 0) {
			timeOut = protocol.getTimeout();
		}

		StringBuilder urlBulder = new StringBuilder();
		urlBulder.append(protocol.getName()).append("://").append(protocol.getHost()).append(":")
				.append(protocol.getPort()).append("/").append(this.getInterfaces())
				.append("?").append(Constants.URL_PARAM_VERSION+"=").append(version)
				.append("&").append(Constants.URL_PARAM_LOADBANLANCE + "=").append(loadBanlance)
				.append("&").append(Constants.URL_PARAM_SRRIALIZE + "=").append(serriza)
				.append("&").append(Constants.URL_PARAM_TIMEOUT + "=").append(timeOut)
				.append("&").append(Constants.INTERFACE_KEY + "=").append(this.getInterfaces());
		final String registurl = urlBulder.toString();
		if (null != this.getRegistrys() && !getRegistrys().isEmpty()) {
			this.getRegistrys().forEach(config -> {
				final String zkAddress = config.getAddress();
				ZkClientFactory factory = ZkClientFactory.instance(zkAddress);
				try {
					// factory.createEphemeral("/test/test", "",true);
					final String path = parentPath + "/" + URLEncoder.encode(registurl);
					logger.info("service url:" + path);
					factory.createEphemeral(path, "", false);
				} catch (NodeExistsException e) {

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}

	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

	}

	private static Integer getRandomPort(String protocol) {
		protocol = protocol.toLowerCase();
		if (RANDOM_PORT_MAP.containsKey(protocol)) {
			return RANDOM_PORT_MAP.get(protocol);
		}
		return Integer.MIN_VALUE;
	}

	private static void putRandomPort(String protocol, Integer port) {
		protocol = protocol.toLowerCase();
		if (!RANDOM_PORT_MAP.containsKey(protocol)) {
			RANDOM_PORT_MAP.put(protocol, port);
		}
	}

	@Override
	public void destroy() throws Exception {
		if (null != this.getRegistrys() && !this.getRegistrys().isEmpty()) {
			for (RegistryConfig config : this.getRegistrys()) {
				config.fulshRemoteConfig();
			}
		}

	}

}
