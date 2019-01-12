package com.my.test.dubbo.config.message.handlers;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.HessianUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.channel.ChannelHandler.Sharable;
@SPI("hessian")
public class HessionSerializeEncode extends MessageToByteEncoder {
	@Override
	protected void encode(ChannelHandlerContext arg0, Object arg1, ByteBuf arg2) throws Exception {
		HessianUtil.instance().MessageToByteBuf(arg2, arg1);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}

}
