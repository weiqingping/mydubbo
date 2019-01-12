package com.my.test.dubbo.config.banlance;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.StringUtils;
public class BalanceFactory {
	private static Map<String, LoadBanlanceProvider> banlanceMap = new ConcurrentHashMap<String, LoadBanlanceProvider>(5);
	static{
		ServiceLoader<LoadBanlanceProvider> loder=ServiceLoader.load(LoadBanlanceProvider.class);
		if(loder!=null){
			Iterator<LoadBanlanceProvider> its=loder.iterator();
			while(null!=its&&its.hasNext()){
				LoadBanlanceProvider blance=its.next();
				SPI spi=blance.getClass().getAnnotation(SPI.class);
		    	if(null!=spi&&StringUtils.isNotEmpty(spi.value())){
					banlanceMap.put(spi.value(), blance);
		    	}
			}
			
		}
	}
	
	public static LoadBanlanceProvider getBanlanceProvider(String banlanceName){
		if(StringUtils.isNotEmpty(banlanceName)&&banlanceMap.containsKey(banlanceName)){
			return banlanceMap.get(banlanceName);
		}else{
			return new RandomRoadBanlanceProvider();
		}
	}

}
