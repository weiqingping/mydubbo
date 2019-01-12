package com.my.test.dubbo.config.registers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegistoryAddressContiner {
	volatile private   ConcurrentHashMap<String, Set<String>> addreeeMapping = new ConcurrentHashMap<String, Set<String>>();
	private AtomicBoolean isWatchRegisty = new AtomicBoolean(false);

	public AtomicBoolean getIsWatchRegisty() {
		return isWatchRegisty;
	}

	public void clearAllService() {
		if (null != addreeeMapping.values() && !(addreeeMapping.values().isEmpty())) {
			addreeeMapping.values().stream().forEach(map -> map.clear());
		}
		addreeeMapping.clear();
	}

	public void clearKeyMap(String key) {
		Set<String> set = addreeeMapping.get(key);
		if (set != null) {
			set.clear();
		}
	}

	public void setKeyMap(String key, Set<String> map) {
		addreeeMapping.put(key, map);
	}

	public void addUrl(String key, String url) {
		// ConcurrentHashMap<String, List<String>> map=addreeeMapping.get(key);
		if (!addreeeMapping.containsKey(key)) {
			Set<String> set = new HashSet<String>();
			addreeeMapping.put(key, set);
		}
		Set<String> set = addreeeMapping.get(key);
		set.add(url);

	}

	public Set<String> getUrlList(String className) {
		Set<String> set = addreeeMapping.get(className);
		return set;
	}

}
