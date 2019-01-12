package com.my.test.dubbo.config.client;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.message.handlers.HeartBeatReqHandler;
import com.my.test.dubbo.config.message.handlers.HelloClientIntHandler;
import com.my.test.dubbo.config.message.handlers.SerializeDecodeEncodeAdapter;
import com.my.test.dubbo.config.message.model.Request;
import com.my.test.dubbo.config.message.model.Response;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

//dubbo://127.0.0.1:20880/com.xx.DemoService
public class NettyClient {

	private static EventLoopGroup workgroup = new NioEventLoopGroup();

	private RefrenceConfig config;
	private Bootstrap strap;
	private ChannelFuture cf1;
	private Response resp;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public NettyClient(RefrenceConfig config, String url) {
		super();
		this.url = url;
		this.initClinet();
		this.config = config;
	}

	public RefrenceConfig getConfig() {
		return config;
	}

	public void setConfig(RefrenceConfig config) {
		this.config = config;
	}

	public void initClinet() {
	}

	void doConnect() {
		try {
			TimeUnit.SECONDS.sleep(5);
			cf1 = strap.connect(CommonUtil.getHost(this.url), CommonUtil.getPort(this.url)).sync();
		} catch (InterruptedException e) {
			throw new RuntimeException("链接远程服务异常" + e.getMessage(), e);

		}
	}

	private String getSerizName() {
		URL url = URL.valueOf(this.url);
		String serializeName = url.getParameter(Constants.URL_PARAM_SRRIALIZE);
		if (StringUtils.isEmpty(serializeName)) {
			serializeName = Constants.DEFAULT_REMOTING_SERIALIZATION;
		}
		return serializeName;
	}

	public Object sengMessage(final Request request) throws Exception {
		Object obj = null;
		try {
			resp = new Response();
			String serName = getSerizName();
			final SerializeDecodeEncodeAdapter adapter = new SerializeDecodeEncodeAdapter(serName);
			strap = new Bootstrap();// 客户端
			strap.group(workgroup).channel(NioSocketChannel.class)// 客户端
																	// -->NioSocketChannel
					.option(ChannelOption.SO_KEEPALIVE, true).handler(new ChannelInitializer<SocketChannel>() {// handler
						@Override
						protected void initChannel(SocketChannel sc) throws Exception {
							ChannelPipeline pipeline = sc.pipeline();
							pipeline.addLast(adapter.newDecodeHandler());
							pipeline.addLast(adapter.newEncodeHandler());
							// pipeline.addLast(new IdleStateHandler(0, 4, 0,
							// TimeUnit.SECONDS));
							// pipeline.addLast(new HeartBeatReqHandler(5));
							pipeline.addLast(new HelloClientIntHandler(resp));
						}
					});

			cf1 = strap.connect(CommonUtil.getHost(this.url), CommonUtil.getPort(this.url)).sync();
			// final ClientCallBack callBack = new ClientCallBack();
			// AttributeKey<ClientCallBack> keys =
			// AttributeKey.valueOf("callBack");
			String methodName = request.getMethodName();
			Class<?>[] argTypes = request.getArugumentTypes();
			final Class type = request.getInterfaceClass().getMethod(methodName, argTypes).getReturnType();
			cf1.channel().attr(Constants.classKey).set(type);
			cf1.channel().writeAndFlush(request);
			// callBack.startWait();
			// obj = callBack.getMessage();

			obj = resp.getBody();
			// System.out.println(Thread.currentThread().getName());
			return obj;
		} catch (Exception exception) {
			throw exception;
		} finally {
			if (null != cf1) {
				cf1.channel().closeFuture().sync();
				cf1.channel().close();
			}

		}

		// workgroup.shutdownGracefully();

	}
}
