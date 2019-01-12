package com.my.test.dubbo.container;

import java.util.List;

import com.my.test.dubbo.config.util.StringUtils;

public class Main {
   private final static String CONFIG_CONTAINER_KEY="dubbo.container";
	public static void main(String[] args) {
		String containerNames=ConfigUtil.getValue(CONFIG_CONTAINER_KEY);
		if(StringUtils.isNotEmpty(containerNames)){
			List<Container> containers;
			try {
				containers = ContianerFactory.getContanerList(containerNames);
				for(Container c:containers){
					c.stop();
					c.start();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}

	}
	


}
