package com.my.test.dubbo.config.serialize;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.StringUtils;

public class SerializeProvider {
  private final static ConcurrentHashMap<String, MessageConvert>mapping=new ConcurrentHashMap<String, MessageConvert>(5);
  static{
		ServiceLoader<MessageConvert> loder=ServiceLoader.load(MessageConvert.class);
		if(loder!=null){
			Iterator<MessageConvert> its=loder.iterator();
			while(null!=its&&its.hasNext()){
				MessageConvert messageConvert=its.next();
				SPI spi=messageConvert.getClass().getAnnotation(SPI.class);
		    	if(null!=spi&&StringUtils.isNotEmpty(spi.value())){
		    		mapping.put(spi.value(), messageConvert);
		    	}
			}
			
		}
	}
  
  public static MessageConvert getMessageConvert(String serizaName){
	  return mapping.get(serizaName);
  }
	
}
