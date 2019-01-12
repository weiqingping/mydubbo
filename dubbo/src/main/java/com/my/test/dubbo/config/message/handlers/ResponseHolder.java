package com.my.test.dubbo.config.message.handlers;

public class ResponseHolder {
	private static ThreadLocal<Object>local;
	static{
		local=new ThreadLocal<Object>();
	}
	
	public static Object get(){
		return local.get();
	}
	
	public static void set(Object obj){
		 local.set(obj);
	}

}
