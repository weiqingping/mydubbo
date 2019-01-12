package com.my.test.dubbo.config.util;

import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.protocol.ProtocolFactory;

public class CommonUtil {
	public static String genernateDubboUrl(ServiceConfig config,ProtocolConfig protocol) {
		StringBuilder keyBuilder = new StringBuilder(genernateServerKey(protocol));
		keyBuilder.append("/").append(config.getInterfaces());
		return keyBuilder.toString();
	}

	public static String genernateMethodServiceKey(ServiceConfig config,ProtocolConfig protocol) {
		StringBuilder keyBuilder = new StringBuilder(genernateDubboUrl(config,protocol));
		if (StringUtils.isNotEmpty(config.getVersion())) {
			keyBuilder.append("?version=").append(config.getVersion());
		}else{
			keyBuilder.append("?version=").append("*");

		}
		return keyBuilder.toString();
	}

	/*
	 * public static String getRegistryAddress(ServiceConfig config){
	 * if(config.getRegistry().getHost().indexOf("://")>=0){ return
	 * config.getRegistry().getHost(); }else{ return
	 * config.getRegistry().getProtocol()+"://"+config.getRegistry().getHost();
	 * } }
	 */
	public static String genernateServerKey(ProtocolConfig protocol) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(ProtocolFactory.getSchema(protocol));
		keyBuilder.append("://");
		keyBuilder.append(ProtocolFactory.getHost(protocol));
		keyBuilder.append(":");
		keyBuilder.append(protocol.getPort());
		return keyBuilder.toString();

	}

	public static String getHost(String link) {
		URL url = URL.valueOf(link);
		String host = url.getHost();
		return host;
	}

	public static String getSchema(String link) {
		URL url = URL.valueOf(link);
		String schema = url.getProtocol();
		return schema;
	}

	public static int getPort(String link) {
		URL url = URL.valueOf(link);
		int port = url.getPort();
		return port;
	}

	public static String genernateMethodServiceKey(URL url){
		StringBuilder sb=new StringBuilder();
		sb.append(url.getProtocol());
		sb.append("://");
		sb.append(url.getAddress());
		sb.append(url.getAbsolutePath());
		String version=url.getParameter("version");
		if(StringUtils.isEmpty(version)){
			version="*";
		}
		sb.append("?version="+version);
		return sb.toString();

	

	}
	
	
	public static void main(String[] args) {
		URL url=URL.valueOf("http://www.baidu.com:8080/abbbb?id=2");
		System.out.println(url.getAbsolutePath());
		System.out.println(url.getAddress());
		System.out.println(url.getHost());

	}
	
	public static String genernateKey(URL url, String interfaces) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(url.getProtocol());
		keyBuilder.append("://");
		keyBuilder.append(url.getHost());
		keyBuilder.append(":");
		keyBuilder.append(url.getPort());
		keyBuilder.append("/");
		keyBuilder.append(interfaces);
		String version = url.getParameter("version");
		if (StringUtils.isNotEmpty(version)) {
			keyBuilder.append("?version=" + version);
		}
		return keyBuilder.toString();
	}

}
