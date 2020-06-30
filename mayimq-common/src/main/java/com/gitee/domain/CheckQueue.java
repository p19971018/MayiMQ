package com.gitee.domain;

import com.gitee.common.io.utils.MayiMqUtils;

public class CheckQueue {

	public static void message(Object msg) {
		MayiMqMessage mqMessage = MayiMqUtils.strToEntity(msg.toString());
		if(null == mqMessage) {
			throw new IllegalArgumentException("Mayi-MQ, MQMessage can not be null.");
		}
		if(null == mqMessage.getKey()) {
			throw new IllegalArgumentException("Mayi-MQ, MQMessage key can not be null.");
		}
		/*if(null == mqMessage.getValue()) {
			throw new IllegalArgumentException("Mayi-MQ, MQMessage message value is not null");
		}*/
		if( null != mqMessage.getValue() && mqMessage.getValue().toString().length() > 1024*1024) {
			throw new IllegalArgumentException("Mayi-MQ, MQMessage message value size not exceed 1024*1024 KB ");
		}
	}

}
