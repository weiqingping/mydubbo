package com.my.test.dubbo.config.serialize;

import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.TypeReference;
import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.HessianUtil;

@SPI("hessian")
public class HessianMessageConvert implements MessageConvert {

	@Override
	public <T> T readObject(InputStream inputStream, Class clas) throws Exception {
		Object obj = HessianUtil.instance().convertInputStreamToObject(inputStream);
		return (T) obj;
	}

	@Override
	public void writeObject(OutputStream outputStream, Object object) throws Exception {
		outputStream.write( HessianUtil.instance().objectToByte(object));
	}

}
