package com.my.test.dubbo.config.banlance;

import java.util.Random;
import java.util.Set;

import com.my.test.dubbo.config.annotation.SPI;

@SPI("random")
public class RandomRoadBanlanceProvider implements LoadBanlanceProvider {

	@Override
	public String banlanceOne(Set<String> urls) {
		Random ra = new Random();
		if(urls!=null && !urls.isEmpty()){
		String[] urlArray = urls.toArray(new String[] {});
			if (urlArray != null && urlArray.length > 0) {
				int index = ra.nextInt(urlArray.length);
				return urlArray[index];
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		for(int i=0;i<100;i++){
		Random ra = new Random();
		System.out.println(ra.nextInt(2));
		}

	}

}
