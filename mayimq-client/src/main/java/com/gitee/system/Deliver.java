package com.gitee.system;

import com.gitee.client.GroupConsumer;
import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.domain.MayiMqMessage;
import com.gitee.domain.Proxy;

/**
 *  动态代理集中处理
 * @author wangchl
 *
 */
public class Deliver {
	
	
	/**
	 *   字符串转换为实体类并交付给处理类
	 * @param parame 消息
	 * @throws Exception 
	 */
	public static void toTreatHand(String parame) throws Exception {
		handTreat((MayiMqMessage) MayiMqUtils.strToEntity(parame));
	}
	
	/**
	 *  字节数组 转换为实体类并交付给处理类
	 * @param parame 消息
	 * @throws Exception 
	 */
	public static void toTreatHand(byte[] parame) throws Exception {
		handTreat((MayiMqMessage) MayiMqUtils.byteToEntity(parame));
	}
	
	/**
	 *  集中处理方法
	 * @param parseObject
	 * @throws Exception 
	 */
	public static void handTreat(MayiMqMessage parseObject) throws Exception {
		Proxy.newInstance(GroupConsumer.getRandomGroup(parseObject.getKey()),parseObject.getValue());
	}
	
	

}
