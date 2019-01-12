package com.my.test.dubbo.config.message.handlers;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.CountDownLatch;

import com.my.test.dubbo.config.client.ClientCallBack;
import com.my.test.dubbo.config.message.model.Response;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.AttributeKey;

@Sharable
public class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
	//private ClientCallBack callBack;
	
	private Response resp;

	public HelloClientIntHandler(Response resp) {
		super();
		this.resp = resp;
	}

	public Response getResp() {
		return resp;
	}

	public void setResp(Response resp) {
		this.resp = resp;
	}

	public HelloClientIntHandler() {
		super();
		//this.callBack = callBack;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//ctx.writeAndFlush(request);
		ctx.fireChannelActive();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		//ResponseHolder.set(msg);
		//ctx.fireChannelRead(msg);
		System.out.println(Thread.currentThread().getName());
		if(msg instanceof String &&"ping".equals(msg)){
			//ctx.writeAndFlush("tick");
		}
		
		resp.setBody(msg);
	/*	AttributeKey<ClientCallBack> keys=AttributeKey.valueOf("callBack");
		if(null!=ctx.channel().attr(keys)&&null!=ctx.channel().attr(keys).get())
		 ctx.channel().attr(keys).get().putMessage(msg);*/
		//callBack.putMessage(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		ctx.flush();
	}

}
