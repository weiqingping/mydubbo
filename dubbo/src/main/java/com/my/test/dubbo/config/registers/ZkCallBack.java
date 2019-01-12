package com.my.test.dubbo.config.registers;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

public interface ZkCallBack {
  public void toDo(String className,String path, ChildData data,Type opType) throws Exception;
}
