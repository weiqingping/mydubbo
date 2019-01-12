package com.my.test.dubbo.config.model;

import java.util.List;

public class AbstractInterfaceConfig {
private String serialize;
private String interfaces;
private long timeout;
private List<RegistryConfig> registrys;
private String version;
	private String id;

	public String getSerialize() {
		return serialize;
	}

	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public List<RegistryConfig> getRegistrys() {
		return registrys;
	}

	public void setRegistrys(List<RegistryConfig> registrys) {
		this.registrys = registrys;
	}



	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
