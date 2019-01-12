package com.my.test.dubbo.config.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.my.test.dubbo.config.message.handlers.MessageHandler;
import com.my.test.dubbo.config.message.handlers.SerializeDecodeEncodeAdapter;
import com.my.test.dubbo.config.message.handlers.ServiceChannelHandlerAdapter;
import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer implements Server {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(StringUtils.class);
	private ServerBootstrap b;
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	private ProtocolConfig protocol;
	 

	public NettyServer(ServiceConfig config,ProtocolConfig protocol) {
      init(config,protocol);
      this.protocol=protocol;
	}

	public void init(ServiceConfig config,ProtocolConfig protocol) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		String serializeName=config.getSerialize();
		if(StringUtils.isEmpty(serializeName)){
			serializeName=protocol.getSerialize();
		}
		
		if(StringUtils.isEmpty(serializeName)){
			serializeName=Constants.DEFAULT_REMOTING_SERIALIZATION;
		}
		final SerializeDecodeEncodeAdapter adapter = new SerializeDecodeEncodeAdapter(serializeName);

		b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
		b.handler(new LoggingHandler(LogLevel.DEBUG));
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new ServiceChannelHandlerAdapter());
				pipeline.addLast(adapter.newDecodeHandler());
				pipeline.addLast(adapter.newEncodeHandler());
				// pipeline.addLast(new IdleStateHandler(5, 0, 0,
				// TimeUnit.SECONDS));
				pipeline.addLast(new MessageHandler());
			}
		});
		b.option(ChannelOption.SO_KEEPALIVE, true);


	}

	public void bind() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					
					URL url=protocol.getUrl();
					ChannelFuture channelFuture = b.bind(new InetSocketAddress(url.getHost(), url.getPort())).sync();

					logger.info("开启dubbo服务监听，host：" + url.getHost() + " port:" + url.getPort());
					channelFuture.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (null != workerGroup) {
						workerGroup.shutdownGracefully();
					}
					if (null != bossGroup) {
						bossGroup.shutdownGracefully();
					}
					System.out.println("called shutdown gracefully...");
				}

			}
		}).start();

	}



}
