package com.my.test.dubbo.config.message.model;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Response {
private CountDownLatch latch=new CountDownLatch(1);
private Object body;

public Object getBody() {
	try {
		latch.await(10, TimeUnit.SECONDS);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		latch.countDown();
	}
	return body;
}

public void setBody(Object body) {
	this.body = body;
	latch.countDown();
}
}
