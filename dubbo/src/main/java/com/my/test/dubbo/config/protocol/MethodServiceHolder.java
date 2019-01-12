package com.my.test.dubbo.config.protocol;

import java.util.concurrent.ConcurrentHashMap;

import com.my.test.dubbo.config.message.handlers.Invoker;


public class MethodServiceHolder {

private static ConcurrentHashMap<String, Object> serverContainer=new ConcurrentHashMap<String, Object>();
private static ConcurrentHashMap<String, Invoker> invokerContainer=new ConcurrentHashMap<String, Invoker>();

	
	public static Object getService(String key){
		return serverContainer.get(key);
	}
	public static void addService(String key,Object server){
		serverContainer.put(key, server);
	}
	
	public static Invoker getInvoker(String key){
		return invokerContainer.get(key);
	}
	public static void addInvoker(String key,Invoker invoker){
		invokerContainer.put(key, invoker);
	}
	
}
