package com.my.test.dubbo.config.message.handlers;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.my.test.dubbo.config.annotation.SPI;

import com.my.test.dubbo.config.util.StringUtils;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class SerializeDecodeEncodeAdapter {
	private String hessian;
	public SerializeDecodeEncodeAdapter(String hessian) {
		super();
		this.hessian = hessian;
	}
	private static Map<String, ChannelHandler> encodeMap = new ConcurrentHashMap<String, ChannelHandler>(5);
	private static Map<String, ChannelHandler> decodeMap = new ConcurrentHashMap<String, ChannelHandler>(5);
	static {
		ServiceLoader<ChannelHandler> loader = ServiceLoader.load(ChannelHandler.class,
				SerializeDecodeEncodeAdapter.class.getClassLoader());
		Iterator<ChannelHandler> its = loader.iterator();
		while (null != its && its.hasNext()) {
			ChannelHandler handler = its.next();
			SPI spi = handler.getClass().getAnnotation(SPI.class);
			if (null != spi && StringUtils.isNotEmpty(spi.value())) {
				if (handler instanceof MessageToByteEncoder) {
					encodeMap.put(spi.value(), handler);
				} else if (handler instanceof ByteToMessageDecoder) {
					decodeMap.put(spi.value(), handler);

				}
			}
		}

	}
	
	public ChannelHandler encodeHandler(){
		return encodeMap.get(hessian);
	}
	
	public ChannelHandler decodeHandler(){
		return decodeMap.get(hessian);
	}
	public ChannelHandler newEncodeHandler() throws InstantiationException, IllegalAccessException{
		return encodeMap.get(hessian).getClass().newInstance();
	}
	
	public ChannelHandler newDecodeHandler() throws InstantiationException, IllegalAccessException{
		return decodeMap.get(hessian).getClass().newInstance();
	}
}
