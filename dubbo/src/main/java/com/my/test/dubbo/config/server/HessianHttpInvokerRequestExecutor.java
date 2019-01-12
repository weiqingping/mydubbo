package com.my.test.dubbo.config.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.serialize.MessageConvert;
import com.my.test.dubbo.config.serialize.SerializeProvider;
import com.my.test.dubbo.config.util.BeanUtils;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

public class HessianHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {
	private final ThreadLocal<URL> urlMapping = new ThreadLocal<URL>();
	private final ThreadLocal<RemoteInvocation> remoteInvocationMapping= new ThreadLocal<RemoteInvocation>();


	public void setUrl(URL url) {
		urlMapping.set(url);
	}

	public URL getUrl() {
		return urlMapping.get();
	}

	public HessianHttpInvokerRequestExecutor() {

	}

	private String getSeriName() {
		String seriName = getUrl().getParameter(Constants.URL_PARAM_SRRIALIZE);
		if (StringUtils.isEmpty(seriName)) {
			seriName = Constants.DEFAULT_REMOTING_SERIALIZATION;
		}
		return seriName;
	}

	private MessageConvert getMessageConvert() {
		String seriName = getSeriName();
		MessageConvert convert = SerializeProvider.getMessageConvert(seriName);
		return convert;
	}

	@Override
	protected void writeRemoteInvocation(RemoteInvocation invocation, OutputStream os) throws IOException {
		MessageConvert convert = getMessageConvert();
		if (null == convert) {
			throw new RemoteException("no messageConvert typed:" + getSeriName());
		}
		try {
			remoteInvocationMapping.set(invocation);
			convert.writeObject(os, invocation);
			os.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RemoteException("serialized object cause a problem [" + e.getMessage());
		} finally {
			if (null != os) {
				os.close();
			}
		}
	}

	@Override
	protected RemoteInvocationResult readRemoteInvocationResult(InputStream is, String codebaseUrl)
			throws IOException, ClassNotFoundException {
		MessageConvert convert = getMessageConvert();
		if (null == convert) {
			throw new RemoteException("no messageConvert typed:" + getSeriName());
		}
		try {
			RemoteInvocationResult result = convert.readObject(is, RemoteInvocationResult.class);
			delaRemoteInvocationResult(result);
			
			return (RemoteInvocationResult) result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RemoteException("serialized object cause a problem [" + e.getMessage());
		}
	}
	
	/**
	 * RemoteInvocation特殊处理
	 * @param in
	 * @throws Exception
	 */
	private  final void delaRemoteInvocationResult(RemoteInvocationResult in) throws Exception{
		if (null != in) {
			RemoteInvocation rmi=remoteInvocationMapping.get();
			String methodName=rmi.getMethodName();
			Class<?>[]parmType=rmi.getParameterTypes();
			String inerfacesName=urlMapping.get().getServiceInterface();
			Method method=Class.forName(inerfacesName).getMethod(methodName, parmType);
			if(null!=method){
				Class<?>returnType=method.getReturnType();
				Object obj=BeanUtils.copyProperties(in.getValue(), returnType);
				in.setValue(obj);
			}
			
		}
	}

}
