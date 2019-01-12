package com.my.test.dubbo.config.registers;

import java.util.List;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import com.my.test.dubbo.config.zk.ZkClientFactory;

public class FreshServiceConfigCallBack implements ZkCallBack {
    private String zkAddress;
	public FreshServiceConfigCallBack(String zkAddress) {
		super();
		this.zkAddress = zkAddress;
	}
	public void toDo(String className,String path, ChildData data,Type opType) throws Exception {
        /*ZkClientFactory factory=new ZkClientFactory(zkAddress);
        List<String>children=factory.listChildren(path);
        if(null!=children&&!children.isEmpty()){
        	RegistryUtil.loadRegistry(className, children, path);
        }*/
	}

}
