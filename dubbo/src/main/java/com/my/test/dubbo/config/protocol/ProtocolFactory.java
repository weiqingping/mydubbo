package com.my.test.dubbo.config.protocol;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.util.StringUtils;

public class ProtocolFactory {
	
	private final static Pattern URL_PATTERN=Pattern.compile("://");
	private static Map<String, ProtocolExport> protocolMap = new ConcurrentHashMap<String, ProtocolExport>(5);
	static {
		ServiceLoader<ProtocolExport> loader=ServiceLoader.load(ProtocolExport.class, ProtocolFactory.class.getClassLoader());
		Iterator<ProtocolExport>its= loader.iterator();
	    while(null!=its&&its.hasNext()){
	    	ProtocolExport export= its.next();
	    	SPI spi=export.getClass().getAnnotation(SPI.class);
	    	if(null!=spi&&StringUtils.isNotEmpty(spi.value())){
	    		protocolMap.put(spi.value(), export);
	    	}
	    }

	}

	public static ProtocolExport get(ProtocolConfig config) {
		String schema=getSchema(config);
		if(StringUtils.isNotEmpty(schema)){
			return protocolMap.get(schema);
		}else{
			throw new IllegalArgumentException("the protocol Argument is vaild");
		}
	}
	
	public static ProtocolExport get(String protocolName) {
		if(StringUtils.isNotEmpty(protocolName)){
			return protocolMap.get(protocolName);
		}else{
			throw new IllegalArgumentException("the protocol Argument is vaild");
		}
	}
	
	public final static String getSchema(ProtocolConfig config){
		if(StringUtils.isNotEmpty(config.getName())){
			return config.getName();
		}else{
			return URL_PATTERN.split(config.getHost())[0];
		}
	}
	
	public final static String getHost(ProtocolConfig config){
		     String[]urls=URL_PATTERN.split(config.getHost());
		     if(urls.length>1){
		    	 return  urls[1];
		     }else{
		    	 return urls[0];
		     }
	}
	
	public static void main(String[] args) {
		String[]str=URL_PATTERN.split("dubbo://aawwwwwww.com");
		System.out.println(str);
	}
}
