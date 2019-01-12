package com.my.test.dubbo.config.registers;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

public class  RegistryUtil {

	/*public  synchronized static void loadRegistry(String className, List<String> children, String parentPath) {
		Map<String, String> map = RegistoryAddressContiner.getMap(className);
		if (map == null) {
			RegistoryAddressContiner.setKeyMap(className, new ConcurrentHashMap<String, String>());
		}
		map = RegistoryAddressContiner.getMap(className);
		for (String str : children) {
			URL url = URL.valueOf(URLDecoder.decode(str));
			String version = StringUtils.isEmpty(url.getParameter("version")) ? "*" : url.getParameter("version");
			map.put(version, parentPath + "/" + str);
		}
	}*/

}
