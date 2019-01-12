package com.my.test.dubbo.config.util;

public enum LoadBanlance {
	RANDOM("random", 1), ROUND_ROBIN("roundRobin", 2),
	LEAST_ACTIVE("leastActive", 3), CONSIS_TENTHASH("consistentHash",4);
	LoadBanlance() {
		this.name();
	}

	String name;
	int value;

	LoadBanlance(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name();
	}

	public int getValue() {
		return this.value;
	}

}
