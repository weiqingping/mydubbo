package com.my.test.dubbo.config.message.model;

import java.io.Serializable;

public class Request implements Serializable {
	private String methodName;
	private String url;
	private String key;
	public Request() {
	}
	private Object[]aruguments;
	private Class<?>[]arugumentTypes;
	private Class<?>interfaceClass;

	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}
	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object[] getAruguments() {
		return aruguments;
	}
	public void setAruguments(Object[] aruguments) {
		this.aruguments = aruguments;
	}
	public Class<?>[] getArugumentTypes() {
		return arugumentTypes;
	}
	public void setArugumentTypes(Class<?>[] arugumentTypes) {
		this.arugumentTypes = arugumentTypes;
	}
	public Request(String name, String url, String key, Object[] aruguments, Class<?>[] arugumentTypes) {
		super();
		this.methodName = name;
		this.url = url;
		this.key = key;
		this.aruguments = aruguments;
		this.arugumentTypes = arugumentTypes;
	}
	public Request(String methodName, String url, String key, Object[] aruguments, Class<?>[] arugumentTypes,
			Class<?> interfaceClass) {
		this.methodName = methodName;
		this.url = url;
		this.key = key;
		this.aruguments = aruguments;
		this.arugumentTypes = arugumentTypes;
		this.interfaceClass = interfaceClass;
	}
	

}
