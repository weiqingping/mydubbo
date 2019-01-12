package com.my.test.dubbo.config.message.handlers;

import java.util.List;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.message.model.Request;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.HessianUtil;
import com.my.test.dubbo.config.util.JsonUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
@SPI("json")
public class JsonSerializeDecode extends ByteToMessageDecoder{

	public final static int MESSAGE_LENGTH=4;
	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1, List<Object> arg2) throws Exception {
		Attribute<Class> clasAttr= arg0.channel().attr(Constants.classKey);
		if(clasAttr.get()!=null){
			Object obj=JsonUtil.instance().byteToMessagse(arg1, clasAttr.get());
			if(null!=obj)
			arg2.add(obj);
		}
	
	}

}
