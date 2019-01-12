package com.my.test.dubbo.config.message.handlers;

import java.util.List;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.HessianUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.channel.ChannelHandler.Sharable;
@SPI("hessian")
public class HessionSerializeDecode extends ByteToMessageDecoder{

	public final static int MESSAGE_LENGTH=4;
	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
		Object obj=HessianUtil.instance().byteToMeaagse(arg1,null);
		if(null!=obj)
		arg2.add(obj);
	}

}
