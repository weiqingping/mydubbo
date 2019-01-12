package com.my.test.dubbo.config.client;

import java.util.concurrent.ConcurrentHashMap;

public class NIOClientHolder {
	static ConcurrentHashMap<String, NettyClient>mapping=new ConcurrentHashMap<String, NettyClient>();
	public static void set(String key,NettyClient client){
		mapping.put(key, client);
	}
	public static NettyClient get(String key){
		return mapping.get(key);
	}

}
