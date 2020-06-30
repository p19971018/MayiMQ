package com.gitee.handler;

/**
 * 消费者工厂类
 * @author wangchl
 *
 */
public class MethodFactoryProducer {

	public static AbstractAchieveMethodFactory getFactory() {
		return new MethodFactory();
		
	}
}
