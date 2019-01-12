package com.my.test.dubbo.config.protocol;

import java.lang.reflect.Method;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.util.URL;

@SPI("rmi")
public class RmiProtocolExport implements ProtocolExport {

	public void export(ServiceConfig config,ProtocolConfig protocol) throws Exception {
		

	}

	public Object invoke(String url,RefrenceConfig config, Method method, Object[] parmters) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
