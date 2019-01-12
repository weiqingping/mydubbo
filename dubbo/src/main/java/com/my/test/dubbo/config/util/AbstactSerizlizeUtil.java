package com.my.test.dubbo.config.util;

import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.message.handlers.HessionSerializeDecode;

import io.netty.buffer.ByteBuf;

public  class AbstactSerizlizeUtil {
	
	public   <T> T byteToMeaagse(ByteBuf byteBuf, TypeReference typeReference) throws IOException{
		return null;
	}

	public   <T> T convertInputStreamToObject(InputStream is) throws IOException{
		return null;
	}
	
	public   byte[] objectToByte(Object obj) throws IOException{
		return null;
	}
	
	
	public  void MessageToByteBuf(ByteBuf out, Object obj) throws Exception {
		byte[] bytes = objectToByte(obj);
		byteToByteBuf(bytes, out);
	}
	
	public  void byteToByteBuf(byte[] bytes, ByteBuf out) {
		int dataLength = bytes.length;
		out.writeInt(dataLength);
		out.writeBytes(bytes);
	}

	public  byte[] byteBufferToByte(ByteBuf buffer) {
		// 出现粘包导致消息头长度不对，直接返回
		if (buffer.readableBytes() < HessionSerializeDecode.MESSAGE_LENGTH) {
			return null;
		}

		buffer.markReaderIndex();
		// 读取消息的内容长度
		int messageLength = buffer.readInt();

		/*
		 * if (messageLength < 0) { ctx.close(); }
		 */

		// 读到的消息长度和报文头的已知长度不匹配。那就重置一下ByteBuf读索引的位置
		if (buffer.readableBytes() < messageLength) {
			buffer.resetReaderIndex();
			return null;
		} else {
			byte[] messageBody = new byte[messageLength];
			buffer.readBytes(messageBody);
			return messageBody;
		}

	}
}
