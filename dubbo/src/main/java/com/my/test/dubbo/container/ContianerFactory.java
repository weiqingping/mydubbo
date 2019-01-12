package com.my.test.dubbo.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import com.my.test.dubbo.config.annotation.SPI;

public class ContianerFactory {
	private static final ConcurrentHashMap<String, Container> containers=new ConcurrentHashMap<String, Container>();
	static{
		synchronized (containers) {
			ServiceLoader<Container>loader=ServiceLoader.load(Container.class, ContianerFactory.class.getClassLoader());
			if(null!=loader.iterator()){
				Iterator<Container> its=loader.iterator();
				while(its.hasNext()){
					Container container=its.next();
					SPI spi=container.getClass().getAnnotation(SPI.class);
					if(null!=spi){
					containers.put(spi.value(), container);
					}
				}
			}
		}
	}
	
	
	
	public final static Container getContaner(String name){
		if(containers.containsKey(name)){
			return containers.get(name);
		}else{
			return null;
		}
	}
	public final static List<Container> getContanerList(String names)throws Exception{
		List<Container>containerList=new ArrayList<Container>(4);
		StringTokenizer tokenizer=new StringTokenizer(names, ",");
		while(tokenizer.hasMoreTokens()){
			Container container=getContaner(tokenizer.nextToken());
			if(null!=container){
			containerList.add(container);
			}
		}
		return containerList;
	}

}
