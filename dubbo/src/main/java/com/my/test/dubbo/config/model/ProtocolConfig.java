package com.my.test.dubbo.config.model;

import com.my.test.dubbo.config.util.URL;

public class ProtocolConfig {
	private String name;
	private String host;
	private int port;
	private long timeout;
	private String loadBanlance;
	private String serialize;
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public String getLoadBanlance() {
		return loadBanlance;
	}
	public void setLoadBanlance(String loadBanlance) {
		this.loadBanlance = loadBanlance;
	}
	public String getSerialize() {
		return serialize;
	}
	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
	public URL getUrl(){
		return URL.valueOf(this.getName()+"://"+this.getHost()+":"+this.getPort());
	}

	

}
