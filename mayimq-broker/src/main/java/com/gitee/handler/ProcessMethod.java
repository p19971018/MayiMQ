package com.gitee.handler;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.gitee.client.MayiMqClientFactory;
import com.gitee.common.emun.MessageType;
import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.context.LoadConfigurationInfo;
import com.gitee.context.MayiMqException;
import com.gitee.domain.MayiMqMessage;
import com.gitee.mutual.database.BerkeleyDatabase;
import com.gitee.mutual.domain.ConsumerInfo;
import com.gitee.mutual.domain.DelayMsgQueue;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

/**
 * 处理方法
 * @author wangchl
 *
 */
public class ProcessMethod extends BerkeleyDatabase<Object> implements MessageHandle{

	private final static Logger logger = LoggerFactory.getLogger(ProcessMethod.class);

	//队列信息
	public static ConcurrentHashMap<String,LinkedBlockingQueue<Object>> consumnerQueue  = new ConcurrentHashMap<String,LinkedBlockingQueue<Object>>();
	//广播信息
	public static  ConcurrentHashMap<String, String> multicastInfo = new ConcurrentHashMap<String, String>();

	//消费者信息
	public static ConcurrentHashMap<String,List<ConsumerInfo>> handlerContext = new ConcurrentHashMap<String,List<ConsumerInfo>>();
	//消费者关系
	public static Map<ChannelId,List<String>> consumerNexus = new ConcurrentHashMap<ChannelId,List<String>>();
	//广播消息缓存
	private static LinkedBlockingQueue<String> broadcastMsg = new LinkedBlockingQueue<String>(5000); 
	//线程状态
	public volatile static boolean threadState = true; 

	//广播模式网卡地址分配
	AtomicInteger incr = new AtomicInteger(0);

	public static void setConsumner_Queue(ConcurrentHashMap<String, LinkedBlockingQueue<Object>> consumnerQueues) {
		consumnerQueue = consumnerQueues;
	}

	public static List<ConsumerInfo>  getHandlerContext(Object key) {
		return handlerContext.get(key);
	}
	public static ConcurrentHashMap<String, List<ConsumerInfo>>  getHandlerContext() {
		return handlerContext;
	}
	
	public static List<String> getConsumerNexus(Object key) {
		return consumerNexus.get(key);
	}
	
	public static void removeConsumerNexus(ChannelId id) {
		consumerNexus.remove(id);
		

	}
	public static ConcurrentHashMap<String, List<ConsumerInfo>> getConsumerNexus() {
		return handlerContext;

	}
	
	public static void removeHandlerContext(Object id) {
		handlerContext.remove(id);
	}

	/**
	 * 队列模式
	 */
	@Override
	public void single(Object[] message) throws InterruptedException {
		MayiMqMessage entity = MayiMqUtils.strToEntity(message[0].toString());
		if(entity.isDurable()) {
			super.save(entity.getKey(),entity.getId(), message[0].toString(),null);
		}else {
			MayiMqClientFactory.putMsg(entity.getKey(), message[0]);
		}
	}


	/**
	 * 延时队列
	 */
	@Override
	public void delay(Object[] message) {
		System.out.println(message[0]);
		DelayMsgQueue.putDelayMsg(message[0]);
	}

	/**
	 * 广播消息
	 */
	@Override
	public void broadcast(Object[] message) throws IOException  {
		broadcastMsg.add(message[0].toString());
		
		/*String messages = message[0].toString();
		MayiMqMessage entity = MayiMqUtils.strToEntity(messages);
		InetAddress inetRemoteAddr = InetAddress.getByName(multicastInfo.get(entity.getKey()));
		MulticastSocket client = new MulticastSocket();
		byte[] bytes = messages.getBytes();
		DatagramPacket sendPack = new DatagramPacket(bytes, bytes.length,inetRemoteAddr,LoadConfigurationInfo.getBroadcastPort());
		client.send(sendPack);
		client.close();*/
	}
	

	
	/**
	 * 广播注册
	 */
	@Override
	public void registeredBroadcast(Object[] message) {
		
		if(null == message[1] ) {
			throw new MayiMqException("ChannelHandlerContext can`t is null.");
		}
		ChannelHandlerContext ctx = (ChannelHandlerContext)message[1];
		if(null == message[0] ) {
			throw new MayiMqException("MayiMQ message can`t is null.");
		}

		String addr =LoadConfigurationInfo.getStartNetwork()+incr.incrementAndGet();
		String key =  MayiMqUtils.strToEntity(message[0].toString()).getKey();
		multicastInfo.put(key, addr);
		MayiMqMessage msg = new MayiMqMessage();
		msg.setMulticastAddr(addr);
		msg.setTransportType(MessageType.HERD);
		msg.setKey(key);
		ctx.channel().writeAndFlush( JSON.toJSONString(msg) + System.getProperty("line.separator"));
		
	}
	
	static {
		new Thread(new Broadcast()).start();
	}

	/**
	 * 消费者注册
	 * @param message
	 */
	public void registeredConsumer(Object[] message) {
		ChannelHandlerContext ctx = (ChannelHandlerContext)message[1];
		ChannelId id = ctx.channel().id();
		MayiMqMessage msg = MayiMqUtils.strToEntity(message[0].toString());
		String key = msg.getKey();
		ConsumerInfo build = new ConsumerInfo.Builder().id(id).handlerContext(ctx).build();
		List<String> list = new ArrayList<String>();
		list.add(key);
		//添加消费者服务器对应关系
		if(consumerNexus.containsKey(id)) {
			consumerNexus.get(id).add(key);
			
		}else {
			consumerNexus.put(id,list);
			
		}
		if(handlerContext.containsKey(key)) {
			handlerContext.get(key).add(build);
		}else {
			List<ConsumerInfo> arrayList = new ArrayList<ConsumerInfo>();
			arrayList.add(build);
			handlerContext.put(key, arrayList);
			this.startSpending(key,ctx,msg.isDurable());
		}

	}
	
	private void startSpending(String key, ChannelHandlerContext ctx , boolean isDurable) {
		new Thread(new Spending(key, ctx,isDurable)).start();
	}
	
	public static class Spending implements Runnable{

		private String key;
		private boolean isDurable;
		private ChannelHandlerContext ctx;

		public Spending(String key, ChannelHandlerContext ctx, boolean isDurable) {
			this.key = key;
			this.ctx = ctx;
			this.isDurable = isDurable;
		}

		@Override
		public void run() {

			ChannelId id = ctx.channel().id();
			if(logger.isInfoEnabled()) {
				logger.info("消费端接入。。。 ,ID:{},名称: {},IP地址为 :{}",id,ctx.name(),ctx.channel().localAddress());
			}
			try {
				while(!Thread.currentThread().isInterrupted()) {
					if(threadState) {
						List<ConsumerInfo> list = handlerContext.get(key);
						if(list.isEmpty()) {
							Thread.currentThread().interrupt();
						}else {
							int random = new Random().nextInt(list.size());
							ConsumerInfo consumerInfo = list.get(random);
							this.ctx = consumerInfo.getHandlerContext();
							if(!ctx.channel().isActive() && handlerContext.get(key).size() == 0) {
								if(handlerContext.get(key).size() == 0) {
									Thread.currentThread().interrupt();
									logger.info("主题 ：{} 暂无活跃消费端消费，正在退出 ",key);
								}
							}else {
								if(isDurable) {
									BerkeleyDatabase<String> database = new BerkeleyDatabase<String>();
									database.openConnection( key);
									database.getKeyAllAndDel(ctx, key);
									Thread.sleep(50);
								}else {
									Object message = MayiMqClientFactory.getMessagePoll(key);
									if( null != message) {
										ctx.channel().writeAndFlush(message.toString() + System.getProperty("line.separator"));
									}
								}
							}
						}
					}
				}
				logger.info("主题 {} 已关闭消费 ",key);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

	public static class Broadcast implements Runnable{

		
		@Override
		public void run() {
			try {
				while(true) {
					String messages =  broadcastMsg.take();
					MayiMqMessage entity = MayiMqUtils.strToEntity(messages);
					InetAddress inetRemoteAddr = InetAddress.getByName(multicastInfo.get(entity.getKey()));
					MulticastSocket client = new MulticastSocket();
					byte[] bytes = messages.getBytes("UTF-8");
					DatagramPacket sendPack = new DatagramPacket(bytes, bytes.length,inetRemoteAddr,LoadConfigurationInfo.getBroadcastPort());
//					client.receive(sendPack);
					client.send(sendPack);
					client.close();
				}
				
			} catch (InterruptedException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
