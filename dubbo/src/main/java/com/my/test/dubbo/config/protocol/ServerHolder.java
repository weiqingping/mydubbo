package com.my.test.dubbo.config.protocol;

import java.util.concurrent.ConcurrentHashMap;

import com.my.test.dubbo.config.server.Server;

public class ServerHolder {

	private static ConcurrentHashMap<String, Server> serverContainer=new ConcurrentHashMap<String, Server>(2);
	
	public static Server getServer(String key){
		return serverContainer.get(key);
	}
	public static void addServer(String key,Server server){
		serverContainer.put(key, server);
	}
}
