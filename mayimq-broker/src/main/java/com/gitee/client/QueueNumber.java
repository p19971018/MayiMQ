package com.gitee.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.gitee.common.utils.StringUtils;

public class QueueNumber {


	private static Map<String,AtomicInteger> QueueCount = new HashMap<String,AtomicInteger>();

	private int count = 0;

	private  LinkedBlockingQueue<Object> obj ;

	public  QueueNumber(Object obj, LinkedBlockingQueue<Object> take) {
		this.obj = take;
		this.count = QueueCount.get(obj).get();
	}

	public static void setQueue(String k) {
		if(StringUtils.isNull(QueueCount.get(k))) 
			addQueue(k);
	}

	private static  void addQueue(String k) {
		QueueCount.put(k, new AtomicInteger());
	}

	public static void addQueueCount(Object k) {
		QueueCount.get(k).incrementAndGet();
	}

	@SuppressWarnings("unused")
	private static void incrCount(String k) {
		QueueCount.get(k).incrementAndGet();
	}

	public int getCount() {
		return count;
	}

	public Object getValue() throws InterruptedException {
		return obj.take();
	}



}
