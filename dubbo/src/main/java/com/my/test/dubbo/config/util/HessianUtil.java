package com.my.test.dubbo.config.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.TypeReference;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.my.test.dubbo.config.message.handlers.HessionSerializeDecode;
import com.my.test.dubbo.config.message.model.Request;

import io.netty.buffer.ByteBuf;

public class HessianUtil extends AbstactSerizlizeUtil {
	private static HessianUtil instance=new HessianUtil();
	private HessianUtil(){
		super();
	}
	public static HessianUtil instance(){
		return instance;
	}
	public  <T> T byteToMeaagse(ByteBuf byteBuf, TypeReference typeReference) throws IOException {
		byte[] bytes = byteBufferToByte(byteBuf);
		if (null == bytes) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		Hessian2Input in = new Hessian2Input(is);
		T person = (T) in.readObject();
		return person;
	}

	public  <T> T convertInputStreamToObject(InputStream is) throws IOException {
		// ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		Hessian2Input in = new Hessian2Input(is);
		try {
			T person = (T) in.readObject();
			return person;
		} finally {
			if (null != is) {
				is.close();
			}
			if (null != in) {
				in.close();
			}
		}

	}



	public  byte[] objectToByte(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Hessian2Output oos = new Hessian2Output(out);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		byte[] bytes = out.toByteArray();
		return bytes;
	}




}
