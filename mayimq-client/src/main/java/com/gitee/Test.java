package com.gitee;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.gitee.aspectj.annotation.lang.Log;
import com.gitee.aspectj.enums.OperatingType;
import com.gitee.common.emun.MessageType;
import com.gitee.domain.MayiMqMessage;
import com.gitee.mq.annotation.IMqConsumer;
import com.gitee.mutual.net.MayiClientHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

@Controller
public class Test {

	volatile int in = 0;
	AtomicInteger in1 = new AtomicInteger(0);

	@RequestMapping("addMsg1")
	@ResponseBody
	public void consumer1(String obj) throws InterruptedException {
		String topic1 = "Topic_1";
		new Thread(new testProducer(in1,topic1)).start();
	}

	@RequestMapping("addMsg")
	@ResponseBody
	public void consumer2(String obj) throws InterruptedException {
		String topic1 = "Topic_2";
		new Thread(new testProducer(in1,topic1)).start();
	}

	@RequestMapping("delayMsg")
	@ResponseBody
	public void delayMsg() throws InterruptedException {
		MayiMqMessage msg = new MayiMqMessage();
		msg.setAddTime(new Date().getTime() );
		msg.setTransportType(MessageType.DELAY);
		msg.setId();
		msg.setKey("Topic_1");
		msg.setValue(in1.incrementAndGet());
		msg.setDelayTime(5000);
		MayiClientHandler.sendMsg(msg);
	}

	/**
	 * 发送广播消息
	 * @param obj
	 * @throws InterruptedException
	 */
	@RequestMapping("sendMulticastMsg")
	@ResponseBody
	public void multicast(String obj) throws InterruptedException {
		String topic1 = "Topic_1";
		new Thread(new testMulticastMsg(in1,topic1)).start();
	}
	/**
	 * 注册广播队列
	 * @param obj
	 * @throws InterruptedException
	 */
	@RequestMapping("sendRegistrationMulticast")
	@ResponseBody
	public void sendMulticast(String obj) throws InterruptedException {
		String topic1 = "Topic_1";
		new Thread(new sendRegistrationMulticast(in1,topic1)).start();
	}



	@Log(title="Topic_2",operatingType=OperatingType.OTHER)
	@IMqConsumer(value="Topic_2")
	public void testConsumer2(Object k) {
		System.out.println("Topic_2：消费消息参数："+k);

	}

	@Log(title="Topic_1",operatingType=OperatingType.OTHER)
	@IMqConsumer(value="Topic_1")
	public void testConsumer1(Object k) {
		System.out.println("Topic_12：消费消息参数："+k);
	}

	@Log(title="Topic_1",operatingType=OperatingType.OTHER)
	@IMqConsumer(value="Topic_1")
	public void testConsumer3(Object k) {
		System.out.println("Topic_111111111111：消费消息参数："+k);
	}

	@RequestMapping("addGroupKey")
	@ResponseBody
	public void testGroupKey() throws Exception {
		new Thread(new testConsumer(in)).start();
		new Thread(new testConsumer1(in)).start();
	}

}

class sendRegistrationMulticast implements Runnable{
	private String key ; 
	AtomicInteger in ;

	public sendRegistrationMulticast(AtomicInteger in, String key) {
		this.in = in;
		this.key = key;
	}

	@Override
	public void run() {
		long time = new Date().getTime();
		try {
			MayiMqMessage msg2 = new MayiMqMessage();
			msg2.setAddTime(new Date().getTime() );
			msg2.setTransportType(MessageType.REGISTRATION_HERD);
			msg2.setKey(key);
			msg2.setValue(in.incrementAndGet());
			MayiClientHandler.sendMsg(msg2);
		} catch (Exception e) {
			System.out.println("myThread.run()"+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("写入数据总耗时："+(new Date().getTime() - time));
	}

}
class testMulticastMsg implements Runnable{
	private String key ; 
	AtomicInteger in ;

	public testMulticastMsg(AtomicInteger in, String key) {
		this.in = in;
		this.key = key;
	}

	@Override
	public void run() {
		try {
			MayiMqMessage msg2 = new MayiMqMessage();
			msg2.setAddTime(new Date().getTime() );
			msg2.setTransportType(MessageType.HERD);
			msg2.setKey(key);
			msg2.setValue(in.incrementAndGet());
			MayiClientHandler.sendMsg(msg2);
		} catch (Exception e) {
			System.out.println("myThread.run()"+e.getMessage());
			e.printStackTrace();
		}
	}

}
class testProducer implements Runnable{
	private String key ; 
	AtomicInteger in ;

	public testProducer(AtomicInteger in, String key) {
		this.in = in;
		this.key = key;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < 5; i++) {
				MayiMqMessage msg2 = new MayiMqMessage();
				msg2.setAddTime(new Date().getTime() );
				msg2.setTransportType(MessageType.SINGLE);
				msg2.setKey(key);
				msg2.setValue(in.incrementAndGet());
				msg2.setDurable(true);
				msg2.setId();
				MayiClientHandler.sendMsg(msg2);
			}

		} catch (Exception e) {
			System.out.println("myThread.run()"+e.getMessage());
			e.printStackTrace();
		}
	}

}
class testConsumer implements Runnable{
	int in ;

	public testConsumer(int in) {
		this.in = in;
	}

	@Override
	public void run() {
		try {
			String topic1 = "Topic_1";
			MayiMqMessage msg2 = new MayiMqMessage();
			msg2.setAddTime(new Date().getTime());
			msg2.setTransportType(MessageType.SINGLE);
			msg2.setKey(topic1);
			msg2.setValue(in);
			ChannelHandlerContext ctx = null;
			try {
				//读取随机服务端信息
				ctx = MayiClientHandler.getRandomServerInfo() ;
				ctx.channel().writeAndFlush(JSON.toJSONString(msg2)  + System.getProperty("line.separator"));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("myThread.run()"+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	

}
class testConsumer1 implements Runnable{

	int in ;
	public testConsumer1(int in) {
		this.in = in;
	}
	@Override
	public void run() {
		try {
			String topic1 = "Topic_2";
			MayiMqMessage msg2 = new MayiMqMessage();
			msg2.setTransportType(MessageType.SINGLE);
			msg2.setKey(topic1);
			msg2.setValue(in);
			ByteBuf msg = null;
			ChannelHandlerContext ctx = null;
			try {
				//读取服务端信息
				ctx = MayiClientHandler.getRandomServerInfo() ;

				String consumerMsg = JSON.toJSONString(msg2) + System.getProperty("line.separator");
				msg = Unpooled.buffer();
				msg.writeBytes(consumerMsg.getBytes());
				ctx.channel().writeAndFlush(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("myThread.run()"+e.getMessage());
			e.printStackTrace();
		}
	}

}



