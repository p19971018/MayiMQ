package com.gitee.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.gitee.context.pattern.AbstractCustomer;
import com.gitee.context.pattern.NullCustomer;
import com.gitee.context.pattern.RealCustomer;


public class MayiMqClientFactory {


	private MayiMqClientFactory() {
	}


	//队列池
	static ConcurrentHashMap<String, LinkedBlockingQueue<Object>> QueuePool = new ConcurrentHashMap<String, LinkedBlockingQueue<Object>>();

	
	public static Object getMessageObject(String k) throws InterruptedException {
		
		LinkedBlockingQueue<Object> take = QueuePool.get(k);
		if(take == null) {
			addQueuePool(k);
		}
		return QueuePool.get(k).take();
	}

	public static AbstractCustomer getCustomer(String k) {
		return customer(k);
	}


	private static void addQueuePool(String k) {
		QueuePool.put(k, new LinkedBlockingQueue<Object>());

	}


	public static void putMsg(String k,Object v) throws InterruptedException {
		if(!QueuePool.containsKey(k)) {
			addQueuePool(k);
		}
		if(null == k) {
			throw new IllegalArgumentException("mayi-MQ, MqMessage Queue name can not be null.");
		}
		QueuePool.get(k).put(v);
	} 



	/**
	 * 空对象模式
	 * @param k
	 * @return 存在则返回键值，否则返回指定空对象
	 */
	private static AbstractCustomer customer(String k) {
		if(QueuePool.containsKey(k)) {
			return new RealCustomer(k);
		}
		return new NullCustomer();

	}









}
