package com.gitee.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 死信处理器
 * @author wangchl
 *
 */
public abstract class AbstractDeadLetterProcessor {

	//死信队列池
	static ConcurrentHashMap<String, LinkedBlockingQueue<Object>> dlq_queue_pool = new ConcurrentHashMap<String, LinkedBlockingQueue<Object>>();
	
	String key ;
	

	public AbstractDeadLetterProcessor(String key) {
		this.key = key;
	}

	protected void putNewQueue() {
		dlq_queue_pool.put(key, new LinkedBlockingQueue<Object>());
	}
	
	private LinkedBlockingQueue<Object> getQueuePool() {
		return dlq_queue_pool.get(key);
	}
	
	private boolean existsQueuePool() {
		return dlq_queue_pool.containsKey(key);
	}
	
	public  void addDlqQueue(Object msg) {
		if(!existsQueuePool()) {
			putNewQueue();
		}
		getQueuePool().add(msg);
		
	}
	/**
	 *  堵塞式消费
	 * @param key
	 * @return message
	 * @throws InterruptedException 
	 */
	public Object takeDlqQueue() throws InterruptedException {
		checkQueue();
		return getQueuePool().take();
	}
	/**
	 *  单次拉取一次消息并消费
	 * @param key 消费主题
	 * @return 消费队列中存在消息则消费，不存在返回空
	 */
	public Object pollDlqQueue() {
		if(!isEmpty()) {
			return getQueuePool().poll();
		}
		return null;
	}

	public boolean isEmpty() {
		return getQueuePool().isEmpty();
	}

	/**
	 * 检查消费队列是否存在
	 */
	private void checkQueue() {
		//先判断消费队列是否存在，不存在则创建一个主题
		if(!existsQueuePool()) {
			putNewQueue();
		}

	}
	

}
