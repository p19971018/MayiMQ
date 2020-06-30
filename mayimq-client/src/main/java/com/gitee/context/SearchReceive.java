package com.gitee.context;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.gitee.mutual.net.MayiClient;


@Component
public class SearchReceive  implements  ApplicationListener<ContextRefreshedEvent>{

	private static boolean status = true;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(status) {
			new Thread(new MayiClient("127.0.0.1",9999)).start();
			status = false;
		}
	}
	/**
	 * 存储消费者信息
	 */
	/* public static void sendConsumer() {

		Map<String, List<NetModel>> groupAll = GroupConsumer.getGroupAll();
		for (String key : groupAll.keySet()) {
			MayiMqMessage mqMessage = new MayiMqMessage();
			mqMessage.setKey(key);
			mqMessage.setTransportType(2);
			try {
				MayiClientHandler.queue.put(mqMessage);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/
}
