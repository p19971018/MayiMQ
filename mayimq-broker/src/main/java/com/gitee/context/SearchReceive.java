package com.gitee.context;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.gitee.mutual.MayiServer;

@Component
public class SearchReceive implements  ApplicationListener<ContextRefreshedEvent>{

	//启动broker
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		new Thread(new MayiServer(LoadConfigurationInfo.port)).start();
	}
}
