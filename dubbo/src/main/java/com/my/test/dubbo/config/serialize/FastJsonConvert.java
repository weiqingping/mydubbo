package com.my.test.dubbo.config.serialize;

import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.HessianUtil;
import com.my.test.dubbo.config.util.JsonUtil;

@SPI("json")
public class FastJsonConvert implements MessageConvert {

	@Override
	public <T> T readObject(InputStream inputStream, Class clas) throws Exception {
		try {
			Object obj = JsonUtil.instance().convertInputStreamToObject(inputStream, clas);
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
	}

	@Override
	public void writeObject(OutputStream outputStream, Object object) throws Exception {
		outputStream.write( JsonUtil.instance().objectToByte(object));

	}

}
