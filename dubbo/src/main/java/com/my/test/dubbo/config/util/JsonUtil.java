package com.my.test.dubbo.config.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.remoting.support.RemoteInvocation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.netty.buffer.ByteBuf;

public class JsonUtil extends AbstactSerizlizeUtil {

	  public final static Charset UTF8     = Charset.forName("UTF-8");

	    private Charset           charset  = UTF8;

	private SerializerFeature[] features = new SerializerFeature[0];
	private static JsonUtil instance =new JsonUtil();
	
	static{

	}
	public static JsonUtil instance(){
		return instance;
	}

	public <T> T byteToMessagse(ByteBuf byteBuf, Class clas) throws IOException {
		byte[] bytes = byteBufferToByte(byteBuf);
		if (null == bytes) {
			return null;
		}
		System.out.println(new String(bytes));
		  return  JSON.parseObject(new String(bytes), clas);

	}

	public <T> T convertInputStreamToObject(InputStream in, Class clas) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		for (;;) {
			int len = in.read(buf);
			if (len == -1) {
				break;
			}

			if (len > 0) {
				baos.write(buf, 0, len);
			}
		}

		byte[] bytes = baos.toByteArray();
		System.out.println("json 数据-------:"+new String(bytes));
		return  JSON.parseObject(new String(bytes),   clas);
		
	}

	@Override
	public byte[] objectToByte(Object obj) throws IOException {
		String text = JSON.toJSONString(obj, features);
		System.out.println("json："+text);
		return text.getBytes(charset);
	}

}
