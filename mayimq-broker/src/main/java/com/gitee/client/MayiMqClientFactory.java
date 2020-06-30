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
	protected static ConcurrentHashMap<String, LinkedBlockingQueue<Object>> QueuePool = new ConcurrentHashMap<String, LinkedBlockingQueue<Object>>();

	public static QueueNumber getMessage(String k) {
		LinkedBlockingQueue<Object> take = QueuePool.get(k);
		if(take == null) {
			addQueuePool(k);
		}
		return message(k,take);
	}
	
	public static Object getMessageObject(String k) throws InterruptedException {
		
		if(customer(k).isNil()) {
			addQueuePool(k);
		}
		return QueuePool.get(k).take();
	}
	
	public static Object getMessagePoll(String k) throws InterruptedException {
		if(customer(k).isNil()) {
			addQueuePool(k);
		}
		return QueuePool.get(k).poll();
	}

	public static AbstractCustomer getCustomer(String k) {
		return customer(k);
	}


	private static void addQueuePool(String k) {
		QueuePool.put(k, new LinkedBlockingQueue<Object>());

	}

	private static QueueNumber message(String k, LinkedBlockingQueue<Object> take) {
		return new QueueNumber(k,take);

	}

	public static void putMsg(String k,Object v) throws InterruptedException {
		if(null == k) {
			throw new IllegalArgumentException("mayi-MQ, MqMessage Queue name can not be null.");
		}
		if(customer(k).isNil()) {
			addQueuePool(k);
		}
		QueuePool.get(k).put(v);
	} 

	@SuppressWarnings("unused")
	private static void incrCount(String k) {
		QueueNumber.addQueueCount(k);
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
