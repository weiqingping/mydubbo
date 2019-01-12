package com.my.test.dubbo.config.message.handlers;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.message.model.Request;
import com.my.test.dubbo.config.util.Constants;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;

public class ServiceChannelHandlerAdapter  extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Attribute<Class> clasAttr= ctx.channel().attr(Constants.classKey);
		if(clasAttr.get()==null){
			clasAttr.setIfAbsent(Request.class);
		}
		super.channelRead(ctx, msg);
		//ctx.fireChannelRead(msg);
	}
	
	
	
}
