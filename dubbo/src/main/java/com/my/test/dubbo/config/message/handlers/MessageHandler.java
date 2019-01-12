package com.my.test.dubbo.config.message.handlers;

import java.lang.reflect.Method;
import java.util.List;
import com.my.test.dubbo.config.message.model.Request;
import com.my.test.dubbo.config.protocol.MethodServiceHolder;
import com.my.test.dubbo.config.util.BeanUtils;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.URL;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

public class MessageHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectCount = 0;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		lossConnectCount=0;
		Object obj = null;
		if (msg instanceof List && null != msg) {
			List list = (List) msg;
			if (!list.isEmpty()) {
				obj = ((List) msg).get(0);
			}
		} else {
			obj = msg;
		}
		if (msg instanceof Request) {
			Request req = (Request) msg;
			String methodName = req.getMethodName();
			String  urlStr=req.getKey();
			URL url=URL.valueOf(urlStr);
			Object[] argnuments = req.getAruguments();
			Class<?>[] pramterType = req.getArugumentTypes();
			Class<?> interfacesClass = req.getInterfaceClass();
			String key=CommonUtil.genernateKey(url, interfacesClass.getName());
			Object proxyObject = MethodServiceHolder.getService(key);
			if(null!=argnuments&&null!=pramterType){
				for(int j=0;j<argnuments.length;j++){
					argnuments[j]=BeanUtils.copyProperties(argnuments[j], pramterType[j]);
				}
			}
			try {
				Method method = interfacesClass.getMethod(methodName, pramterType);
				if (null == method) {
					throw new RuntimeException("no such method:" + methodName);
				}
				Object result = method.invoke(proxyObject, argnuments);
				if (null != result) {
					ctx.writeAndFlush(result).addListener(ChannelFutureListener.CLOSE);
				}
			} catch (Exception e) {
				ctx.close();
				throw new RuntimeException(e.getMessage(),e);
			}
			
		}else if(msg instanceof String &&"ping".equals(msg)){
			ctx.writeAndFlush("ping").addListener(ChannelFutureListener.CLOSE);
			System.out.println("--------------tick------------");
		}

	}
	


	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
/*	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
		 System.out.println("已经5秒未收到客户端的消息了！");
	        if (evt instanceof IdleStateEvent){
	            IdleStateEvent event = (IdleStateEvent)evt;
	            if (event.state()== IdleState.READER_IDLE){
	                lossConnectCount++;
	                if (lossConnectCount>2){
	                    System.out.println("关闭这个不活跃通道！");
	                    ctx.channel().close();
	                }
	            }
	        }else {
	            super.userEventTriggered(ctx,evt);
	        }


    }  */
}
