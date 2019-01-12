package com.my.test.dubbo.config.message.model;

import java.io.Serializable;

public class HeartBeatMessage implements Serializable {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
