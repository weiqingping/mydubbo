package com.my.test.dubbo.config.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartFactoryBean;

import com.my.test.dubbo.config.client.proxy.ClientProxy;
import com.my.test.dubbo.config.registers.ZkCallBack;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

public class RefrenceConfig extends AbstractInterfaceConfig implements SmartFactoryBean, InitializingBean {
	private String url;
	private String loadBanlance;
	private ProtocolConfig protocol;

	public ProtocolConfig getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolConfig protocol) {
		this.protocol = protocol;
	}

	public String getLoadBanlance() {
		return loadBanlance;
	}

	public void setLoadBanlance(String loadBanlance) {
		this.loadBanlance = loadBanlance;
	}

	private static AtomicBoolean isWatchRegisty = new AtomicBoolean(false);

	private static ConcurrentHashMap<String, ZkCallBack> zkBackMap = new ConcurrentHashMap<String, ZkCallBack>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Class<?> getObjectType() {
		try {
			return Class.forName(this.getInterfaces(), false, RefrenceConfig.class.getClassLoader());
		} catch (Exception e) {
			return null;
		}
	}

	public Object getObject() throws Exception {
		return ClientProxy.getObject(Class.forName(getInterfaces()), this);
	}

	public boolean isEagerInit() {

		return false;
	}

	public boolean isSingleton() {

		return true;
	}

	public boolean isPrototype() {

		return false;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initProperty();
		watchRegistry();
	}

	private void watchRegistry() throws Exception {
		if (null != this.getRegistrys() && !this.getRegistrys().isEmpty()) {
			RegistryConfig regis = this.getRegistrys().get(0);
			if (!zkBackMap.containsKey(this.getInterfaces())) {
				regis.addListener(this.getInterfaces());
				zkBackMap.put(getInterfaces(), regis);

			}
			regis.loadService(getInterfaces());

		}

	}

	private void initProperty() {
		if (null == this.getProtocol()) {
			if (StringUtils.isNotEmpty(this.getUrl())) {
				ProtocolConfig pro = new ProtocolConfig();
				URL url = URL.valueOf(this.getUrl());
				pro.setHost(url.getHost());
				pro.setName(url.getProtocol());
				pro.setPort(url.getPort());
				pro.setSerialize(url.getParameter(Constants.URL_PARAM_SRRIALIZE));
				this.setProtocol(pro);
			}
		}
		if (StringUtils.isEmpty(this.getLoadBanlance())) {
			this.setLoadBanlance(this.getProtocol().getLoadBanlance());
		}
		if (this.getTimeout() <= 0) {
			this.setTimeout(this.getProtocol().getTimeout());
		}
	}

}
