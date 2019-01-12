package com.my.test.dubbo.config.server.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

public class ServiceProxy implements InvocationHandler {

	private Object target;
	
	
	public ServiceProxy(Object target) {
		this.target = target;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(target, args);
	}
	
	public static Object instance(Object target){
		return Proxy.newProxyInstance(ServiceProxy.class.getClassLoader(), target.getClass().getInterfaces(), new ServiceProxy(target));
	}

}
