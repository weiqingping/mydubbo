package com.my.test.dubbo.config.message.handlers;

import java.awt.TrayIcon.MessageType;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatReqHandler extends ChannelDuplexHandler {

	private int beatTime;
	private int curTime = 0;
	public HeartBeatReqHandler(int beatTime) {
		super();
		this.beatTime = beatTime;
	}
	/**
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext,
	 *      java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent){
	        IdleStateEvent event = (IdleStateEvent)evt;
	        if (event.state() == IdleState.READER_IDLE) {
				System.out.println("read 空闲");
				ctx.disconnect();
			}
	        else if (event.state()== IdleState.WRITER_IDLE){
				System.out.println("write 空闲");
	            if (curTime<beatTime){
	                curTime++;
	                ctx.writeAndFlush("ping");
	            }
	        }
	    }

	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		curTime++;
		ctx.fireChannelRead(msg);
	}

	

}

