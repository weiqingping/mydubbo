package com.my.test.dubbo.config.protocol;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.cglib.proxy.MethodProxy;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.client.NIOClientHolder;
import com.my.test.dubbo.config.client.NettyClient;
import com.my.test.dubbo.config.message.handlers.Invoker;
import com.my.test.dubbo.config.message.model.Request;
import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.server.NettyServer;
import com.my.test.dubbo.config.server.Server;
import com.my.test.dubbo.config.server.proxy.ServiceProxy;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

@SPI("dubbo")
public class DubboProtocolExport implements ProtocolExport {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StringUtils.class);

	public void export(ServiceConfig config,ProtocolConfig protocol) throws Exception {
		String methodServiceKey = CommonUtil.genernateMethodServiceKey(config,protocol);
		String dubboUrl=CommonUtil.genernateDubboUrl(config,protocol);
		/*Object proxyObject=ServiceProxy.instance(config.getRef());
		if (null == MethodServiceHolder.getService(methodServiceKey)) {
			MethodServiceHolder.addService(methodServiceKey, proxyObject);
			logger.info("dubbo 服务 开启:"+dubboUrl);
		}*/
		Object proxtObj=MethodServiceHolder.getService(methodServiceKey);
		if(null==proxtObj){
			proxtObj=ServiceProxy.instance(config.getRef());
			MethodServiceHolder.addService(methodServiceKey, proxtObj);
		}
		String serverKey = CommonUtil.genernateServerKey(protocol);
		Server server = ServerHolder.getServer(serverKey);
		if (null == server) {
			server = new NettyServer(config,protocol);
			ServerHolder.addServer(serverKey, server);
			server.bind();
		}

	}

	class InvokerInterceptor implements MethodInterceptor {

		private String methodServiceKey;

		public InvokerInterceptor(String methodServiceKey) {
			this.methodServiceKey = methodServiceKey;
		}

		public Object invoke(MethodInvocation invocation) throws Throwable {

			return invocation.proceed();
		}

	}

	public Object invoke(String url,RefrenceConfig config, Method method,Object[]parmters) throws Exception {
		//String key = url;
		if (StringUtils.isNotEmpty(config.getVersion())) {
			URL uri=URL.valueOf(url).addParameter(Constants.URL_PARAM_VERSION, config.getVersion());
			url=uri.toString();
		}
		Request request = new Request(method.getName(), url,url, parmters, method.getParameterTypes(),Class.forName(config.getInterfaces()));
	
	/*	String clientKey=host+":"+port;
		NettyClient client=NIOClientHolder.get(clientKey);
		if(client==null){
			client=new NettyClient(config);
			NIOClientHolder.set(clientKey, client);
		}*/
		
		return new NettyClient(config,url).sengMessage(request);
	}

}
