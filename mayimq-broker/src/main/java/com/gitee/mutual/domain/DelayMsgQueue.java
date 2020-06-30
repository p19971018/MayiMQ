package com.gitee.mutual.domain;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.gitee.common.emun.MessageType;
import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.domain.MayiMqMessage;
import com.gitee.mutual.MayiServerHandler;

import io.netty.channel.Channel;

/**
 *类说明：将信息推入队列
 */
public class DelayMsgQueue {
	
	private final static Logger logger = LoggerFactory.getLogger(DelayMsgQueue.class);
	
	private volatile static BlockingQueue<MayiMqMessage> queueMsg = new DelayQueue<MayiMqMessage>();


	static {
		new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						MayiMqMessage message = DelayMsgQueue.queueMsg.take().getData();
						message.setTransportType(MessageType.SINGLE);
						String key = message.getKey();
						ConsumerInfo consumerInfo =  MayiServerHandler.getHandlerContext(key).get(0);
						Channel channel = consumerInfo.getHandlerContext().channel();
						channel .writeAndFlush(JSON.toJSONString(message)+System.getProperty("line.separator"));
						if(logger.isDebugEnabled()) {
								logger.info("{} 消费   {}" + "",channel.localAddress(),key);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public static void putDelayMsg(Object message) {
		MayiMqMessage entity = MayiMqUtils.strToEntity(message.toString());
		try {
			queueMsg.put(new MayiMqMessage(entity.getDelayTime(),entity));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}