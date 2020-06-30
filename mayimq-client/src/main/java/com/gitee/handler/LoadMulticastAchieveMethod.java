package com.gitee.handler;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.system.Deliver;

/**
 * 广播模式注册及消息监听
 * @author wangchl
 *
 */
public class LoadMulticastAchieveMethod implements Runnable,AchieveMethod{

	//广播消息缓存
	private static LinkedBlockingQueue<byte[]> broadcastMsg = new LinkedBlockingQueue<byte[]>(5000); 
		
	@Override
	public void handleMethod(String key, Object param) {
		new Thread(new LoadMulticastAchieveMethod(param)).start();
	}
	
	static {
		new Thread(new Broadcast()).start();
	}
	
	public LoadMulticastAchieveMethod() {
	}

	public LoadMulticastAchieveMethod(Object param) {
		this.param = param;
	}

	private Object param;

	@Override
	public void run() {
		try {
			InetAddress inetRemoteAddr = InetAddress.getByName(MayiMqUtils.strToEntity(param.toString()).getMulticastAddr());

			DatagramPacket recvPack = new DatagramPacket(new byte[1024], 1024);

			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket(8888);

			/*
			 如果是发送数据报包,可以不加入多播组; 如果是接收数据报包,必须加入多播组; 这里是接收数据报包,所以必须加入多播组;
			 */
			server.joinGroup(inetRemoteAddr);

			for(;;) {
				server.receive(recvPack);
				byte[] recvByte = Arrays.copyOfRange(recvPack.getData(), 0, recvPack.getLength());
				//在高并发的时候数据如为及时应答会造成数据丢失,采用临时缓存异步执行
				broadcastMsg.add(recvByte);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class Broadcast implements Runnable {
		@Override
		public void run() {
			for (;;) {
				try {
					Deliver.toTreatHand(broadcastMsg.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	




}
