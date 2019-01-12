package com.my.test.dubbo.config.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

public class BeanUtils {

	public static Object copyProperties(Object arg,Class<?> type) throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
		if (null != arg && null != type) {
         if(!type.isAssignableFrom(arg.getClass())){
        	 if(arg instanceof Map){
        		 Map<String,Object> argMap=(Map<String,Object>)arg;
 				Object o=type.newInstance();
        		for(Entry<String, Object>entry:argMap.entrySet()){
        			String key=entry.getKey();
        			Object obj=entry.getValue();
        			PropertyDescriptor descriptor=new PropertyDescriptor(key, type);
        			Method writeMethod=descriptor.getWriteMethod();
        			if(!Map.class.isAssignableFrom(writeMethod.getParameterTypes()[0])&&Map.class.isAssignableFrom(obj.getClass()) ){
        				Object childObj=copyProperties(obj, writeMethod.getParameterTypes()[0]);
        				writeMethod.invoke(o, childObj);
        				
        			}else{
        				writeMethod.invoke(o, obj);
        			}
        		}
        		
        		return o;
        		 
        	 }
         }
		}
		
		return arg;
	
	}
    
}
