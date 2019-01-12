package com.my.test.dubbo.config.serialize;

import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.TypeReference;

public interface MessageConvert {
	public <T> T readObject(InputStream inputStream,Class clas) throws Exception;
	public void writeObject(OutputStream outputStream,Object object) throws Exception;

}
