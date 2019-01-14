package com.my.test.dubbo.config.protocol;

import java.lang.reflect.Method;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.server.HessianHttpInvokerRequestExecutor;
import com.my.test.dubbo.config.server.HessianHttpInvokerServiceExporter;
import com.my.test.dubbo.config.server.JettyHttpServer;
import com.my.test.dubbo.config.server.RequestHttpHandler;
import com.my.test.dubbo.config.server.Server;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

@SPI("http")
public class HttpProtocolExport implements ProtocolExport {

	final static HessianHttpInvokerRequestExecutor executor=new HessianHttpInvokerRequestExecutor();

	public void export(ServiceConfig config,ProtocolConfig protocol) throws Exception {
		 final HessianHttpInvokerServiceExporter httpServiceExporter = new HessianHttpInvokerServiceExporter();
	        httpServiceExporter.setServiceInterface(Class.forName(config.getInterfaces()));
	        httpServiceExporter.setService(config.getRef());
	        try {
	            httpServiceExporter.afterPropertiesSet();
	            String methodServiceKey = CommonUtil.genernateMethodServiceKey(config,protocol);
	    		Object proxtObj=MethodServiceHolder.getService(methodServiceKey);
	    		if(null==proxtObj){
	    			MethodServiceHolder.addService(methodServiceKey, httpServiceExporter);
	    		}
	    		
	    		String serverKey = CommonUtil.genernateServerKey(protocol);
	    		Server server = ServerHolder.getServer(serverKey);
	    		if (null == server) {
	    			server = new JettyHttpServer(protocol.getUrl(),new RequestHttpHandler());
	    			ServerHolder.addServer(serverKey, server);
	    			server.bind();
	    		}
	    		
	    		
	        } catch (Exception e) {
	            throw e;
	        }
	        

	}

	public Object invoke(String url,RefrenceConfig config, Method method, Object[] parmters) throws Exception {
		if (StringUtils.isNotEmpty(config.getVersion())) {
			URL uri=URL.valueOf(url).addParameter(Constants.URL_PARAM_VERSION, config.getVersion());
			url=uri.toString();
		}
		executor.setUrl(URL.valueOf(url));
		HttpInvokerProxyFactoryBean factortBean=new HttpInvokerProxyFactoryBean();
		factortBean.setHttpInvokerRequestExecutor(executor);
		factortBean.setServiceInterface(Class.forName(config.getInterfaces()));
		factortBean.setServiceUrl(url);
		factortBean.afterPropertiesSet();
		Object obj=factortBean.getObject();
		return method.invoke(obj, parmters);
	}

}
