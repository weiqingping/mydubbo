package com.my.test.dubbo.config.server;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.serialize.MessageConvert;
import com.my.test.dubbo.config.serialize.SerializeProvider;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

public class HessianHttpInvokerServiceExporter
		extends org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter {

	@Override
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is)
			throws IOException, ClassNotFoundException {
		MessageConvert convert = getMessageConvert(request);
		if (null == convert) {
			throw new RemoteException("no messageConvert typed:" + getSeriName(request));
		}
		try {
			RemoteInvocation obj = convert.readObject(is, RemoteInvocation.class);
			delaRemoteInvocation(obj);
			return obj;
		} catch (Exception e) {
			throw new RemoteException("Deserialized object cause a problem [" + e.getMessage());
		}
	}

	/**
	 * RemoteInvocation特殊处理
	 * @param in
	 * @throws Exception
	 */
	private  final void delaRemoteInvocation(RemoteInvocation in) throws Exception{
		if (null != in) {
			Object[] argus = in.getArguments();
			Class<?>[] paramtersType = in.getParameterTypes();
			if (null != argus && null != paramtersType) {
				for (int j = 0; j < argus.length; j++) {
					
					Object arg = argus[j];
					Class<?> type = paramtersType[j];
					if (null != arg && null != type) {
						Object obj=copyProperties(arg, type);
						argus[j]=obj;
					}
					
				}
			}
		}
	}
	

	
	
	private final Object copyProperties(Object arg,Class<?> type) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
		if (null != arg && null != type) {
         if(!type.isAssignableFrom(arg.getClass())){
        	 if(arg instanceof Map){
        		 Map<String,Object> argMap=(Map<String,Object>)arg;
 				Object o=type.newInstance();
        		for(Entry<String, Object>entry:argMap.entrySet()){
        			String key=entry.getKey();
        			Object obj=entry.getValue();
        			PropertyDescriptor descriptor=new PropertyDescriptor(key, type);
        			Method writeMethod=descriptor.getWriteMethod();
        			if(!Map.class.isAssignableFrom(writeMethod.getParameterTypes()[0])&&Map.class.isAssignableFrom(obj.getClass()) ){
        				Object childObj=copyProperties(obj, writeMethod.getParameterTypes()[0]);
        				writeMethod.invoke(o, childObj);
        				
        			}else{
        				writeMethod.invoke(o, obj);
        			}
        		}
        		
        		return o;
        		 
        	 }
         }
		}
		
		return arg;
	
	}
    
	

	private String getSeriName(HttpServletRequest request) {
		StringBuffer sb = request.getRequestURL();
		String path = request.getQueryString();
		sb.append("?").append(path);
		URL url = URL.valueOf(sb.toString());
		String seriName = url.getParameter(Constants.URL_PARAM_SRRIALIZE);
		if (StringUtils.isEmpty(seriName)) {
			seriName = Constants.DEFAULT_REMOTING_SERIALIZATION;
		}
		return seriName;
	}

	private MessageConvert getMessageConvert(HttpServletRequest request) {
		String seriName = getSeriName(request);
		MessageConvert convert = SerializeProvider.getMessageConvert(seriName);
		return convert;
	}

	@Override
	protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response,
			RemoteInvocationResult result, OutputStream os) throws IOException {
		MessageConvert convert = getMessageConvert(request);
		if (null == convert) {
			throw new RemoteException("no messageConvert typed:" + getSeriName(request));
		}
		try {
			convert.writeObject(os, result);
			os.flush();
		} catch (Exception e) {
			throw new RemoteException("serialized object cause a problem [" + e.getMessage());
		} finally {
			if (null != os) {
				os.close();
			}
		}

	}

}
