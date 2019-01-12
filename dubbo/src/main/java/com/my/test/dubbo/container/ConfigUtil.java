package com.my.test.dubbo.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.omg.Messaging.SyncScopeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtil {
	private static final String CONFIG_FILE_PATH = "/dubbo.properties";
	private static Properties pros = new Properties();

	private static final Logger logger = LoggerFactory.getLogger(SpringContainer.class);

	static {
		synchronized (pros) {
			try {
				pros.load(ConfigUtil.class.getResourceAsStream(CONFIG_FILE_PATH));
			} catch (IOException e) {
				logger.error("load file {0} error for {1}", CONFIG_FILE_PATH, e.getMessage());
			}
		}
	}

	public static String getValue(String key) {
		if (pros.containsKey(key)) {
			return pros.getProperty(key);
		}
		return null;
	}

	public static void setValue(String key, String value) {
		pros.setProperty(key, value);
	}
}
