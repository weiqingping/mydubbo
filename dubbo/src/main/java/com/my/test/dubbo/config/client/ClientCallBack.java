package com.my.test.dubbo.config.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientCallBack {
	private CountDownLatch latch;
	private Object message;
	public ClientCallBack(CountDownLatch latch, Object message) {
		super();
		this.latch = latch;
		this.message = message;
	}
	public ClientCallBack() {
		latch=new CountDownLatch(1);
	}
	
	public   void  startWait() throws InterruptedException{
		latch.await(10, TimeUnit.SECONDS);
	}
	
	public void putMessage(Object message){
		this.message=message;
		latch.countDown();
	}
	public Object getMessage() {
		return message;
	}

	

}
