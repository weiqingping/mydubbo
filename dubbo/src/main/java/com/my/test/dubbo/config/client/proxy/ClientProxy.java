package com.my.test.dubbo.config.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.curator.shaded.com.google.common.collect.Lists;

import com.my.test.dubbo.config.banlance.BalanceFactory;
import com.my.test.dubbo.config.banlance.LoadBanlanceProvider;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.protocol.ProtocolExport;
import com.my.test.dubbo.config.protocol.ProtocolFactory;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.Constants;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.config.util.URL;

public class ClientProxy implements InvocationHandler {

	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private RefrenceConfig config;
	private final static int RETRY_TIME_MAX = 5;
	private final static long SLEEP_TIME = 5000l;

	public ClientProxy(RefrenceConfig config) {
		super();
		this.config = config;
	}

	public RefrenceConfig getConfig() {
		return config;
	}

	public void setConfig(RefrenceConfig config) {
		this.config = config;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (!"wait".equals(method.getName()) && !"toString".equals(method.getName())
				&& !"hashCode".equals(method.getName()) && !"notifyAll".equals(method.getName())
				&& !"notifyAll".equals(method.getName()) && !"notify".equals(method.getName())
				&& !"equals".equals(method.getName()) && !"getClass".equals(method.getName())) {
			try {
				if (StringUtils.isEmpty(config.getUrl())) {
					String url = loadBanlanceUrl();
					if (!StringUtils.isEmpty(url)) {
						ProtocolExport export = ProtocolFactory.get(CommonUtil.getSchema(url));
						return export.invoke(url, config, method, args);
					} else {
						throw new RuntimeException("no service url mapping for" + config.getInterfaces());
					}
				} else {
					ProtocolExport export = ProtocolFactory.get(CommonUtil.getSchema(config.getUrl()));
					return export.invoke(config.getUrl(), config, method, args);
				}
			} catch (Exception e) {
				if ("AnnotatedConnectException".equals(e.getClass().getSimpleName())) {
					System.out.println("----AnnotatedConnectException-----");
					int restryTimes = atomicInteger.getAndDecrement();
					if (restryTimes < RETRY_TIME_MAX) {
						if (null != this.config.getRegistrys() && !this.config.getRegistrys().isEmpty()) {
							this.config.getRegistrys().get(0).loadService(config.getInterfaces());
						}
						Thread.sleep(SLEEP_TIME);
						return invoke(proxy, method, args);
					} else {
						throw e;
					}
				} else {
					e.printStackTrace();
					throw e;
				}
			}

		} else {
			return method.invoke(proxy, args);
		}

	}

	public String loadBanlanceUrl() {
		Set<String> result = new HashSet<String>();
		Set<String> urls;
		if (null != this.config.getRegistrys() && !this.config.getRegistrys().isEmpty()) {
			urls = this.config.getRegistrys().get(0).getContailner().getUrlList(this.config.getInterfaces());
			if (null != urls && !urls.isEmpty()) {
				result.clear();
				Iterator<String> its = urls.stream().filter(str -> {
					URL url = URL.valueOf(str);
					String pro = url.getProtocol();
					if (StringUtils.isEquals(this.config.getProtocol().getName(), pro)) {
						return true;
					}
					return false;

				}).iterator();

				;
				if (its != null && its.hasNext()) {
					urls = new HashSet<>(Lists.newArrayList(its));
				} else {
					urls.clear();
				}

				if (StringUtils.isNotEmpty(this.config.getVersion())) {
					urls.stream().forEach(urlstr -> {
						URL url = URL.valueOf(urlstr);
						String version = url.getParameter(Constants.URL_PARAM_VERSION);
						if (StringUtils.isEquals(this.config.getVersion(), version)) {
							result.add(url.toString());
						}
					});
					if(result.isEmpty()){
						throw new RuntimeException("no service for version:"+this.config.getVersion()+" in the service:"+this.config.getInterfaces());
					}
					
				}

				if (result.isEmpty()) {
					urls.stream().forEach(urlstr -> {
						URL url = URL.valueOf(urlstr);
						String version = url.getParameter(Constants.URL_PARAM_VERSION);
						if (StringUtils.isEquals("*", version)) {
							result.add(url.toString());
						}
					});

				}

				if (result.isEmpty()) {
					result.addAll(urls);
				}
				//筛选数据，保证每个服务器只存一条地址信息
				Map<String,String>filterMap=new HashMap<>();
				if(null!=result&&!result.isEmpty()){
					result.forEach(str->{
						URL u = URL.valueOf(str);
						String host=u.getHost();
						if(filterMap.containsKey(host)){
							String oldVersion=URL.valueOf(filterMap.get(host)).getParameter(Constants.URL_PARAM_VERSION);
							String version=u.getParameter(Constants.URL_PARAM_VERSION)==null?"*":u.getParameter(Constants.URL_PARAM_VERSION);
							if(version.compareTo(oldVersion)>0){
								filterMap.put(host, str);
							}
						}else{
							filterMap.put(host, str);
						}
                        
						
					});
				}

		

				if (null != filterMap && !filterMap.isEmpty()) {
					result.clear();
					result.addAll(filterMap.values());
				}
			}
		}
		setLoadBanlanceName(result);
		LoadBanlanceProvider loadBanlanceProvider = BalanceFactory.getBanlanceProvider(config.getLoadBanlance());
		String url = loadBanlanceProvider.banlanceOne(result);
		/*
		 * if (StringUtils.isEmpty(url)) { throw new RuntimeException(
		 * "no service url mapping for "+this.getInterfaces()); }
		 */
		// config.setUrl(URL.decode(url));
		return url;

	}

	public void setLoadBanlanceName(Set<String> urls) {
		if (StringUtils.isEmpty(this.config.getLoadBanlance())) {
			if (null != urls && !urls.isEmpty()) {
				if (StringUtils.isEmpty(this.config.getLoadBanlance())) {
					Map<String, AtomicInteger> banlanceSort = new HashMap<String, AtomicInteger>();

					urls.stream().forEach(str -> {
						URL url = URL.valueOf(str);
						String banlance = url.getParameter(Constants.URL_PARAM_LOADBANLANCE);
						if (StringUtils.isEmpty(banlance)) {
							banlance = "*";
						}
						if (!banlanceSort.containsKey(banlance)) {
							banlanceSort.put(banlance, new AtomicInteger(1));
						} else {
							banlanceSort.get(banlance).incrementAndGet();
						}
					});

					List<Map.Entry<String, AtomicInteger>> list = new ArrayList<Map.Entry<String, AtomicInteger>>(
							banlanceSort.entrySet());
					list.sort((c1, c2) -> Integer.valueOf(c2.getValue().get()).compareTo(c1.getValue().get()));
					if (null != list && !list.isEmpty()) {
						config.setLoadBanlance(list.get(0).getKey());
					} else {
						config.setLoadBanlance("random");
					}
				}

			}
		}

	}

	public static <T> T getObject(Class<T> cla, RefrenceConfig config) {
		return (T) Proxy.newProxyInstance(ClientProxy.class.getClassLoader(), new Class[] { cla },
				new ClientProxy(config));
	}

}
