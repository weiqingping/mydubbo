package com.my.test.dubbo.config.message.handlers;

public class Invoker {
 private Object target;
 private Class interfaceClass;
 private String version;

public String getVersion() {
	return version;
}

public void setVersion(String version) {
	this.version = version;
}

public Invoker(Object target, Class interfaceClass, String version) {
	super();
	this.target = target;
	this.interfaceClass = interfaceClass;
	this.version = version;
}

public Invoker(Object target, Class interfaceClass) {
	super();
	this.target = target;
	this.interfaceClass = interfaceClass;
}

public Class getInterfaceClass() {
	return interfaceClass;
}

public void setInterfaceClass(Class interfaceClass) {
	this.interfaceClass = interfaceClass;
}

public Object getTarget() {
	return target;
}

public void setTarget(Object target) {
	this.target = target;
}
 }
