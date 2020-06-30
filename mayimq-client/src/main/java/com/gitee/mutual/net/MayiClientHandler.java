package com.gitee.mutual.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.gitee.client.GroupConsumer;
import com.gitee.common.emun.MessageType;
import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.domain.MayiMqMessage;
import com.gitee.domain.NetModel;
import com.gitee.handler.AchieveMethod;
import com.gitee.handler.MethodFactoryProducer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 *   消费者核心处理
 * @author wangchl
 *
 */
public class MayiClientHandler extends SimpleChannelInboundHandler<ByteBuf> {


	private final static Logger logger = LoggerFactory.getLogger(MayiClientHandler.class);

	/**服务端信息 */
	private static volatile List<ChannelHandlerContext> server_info = new ArrayList<>();



	public static ChannelHandlerContext getRandomServerInfo() {
		return server_info.get(server_info.size()-1);
	}

	//连接为长连接,不要关闭
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		String msgs = msg.toString(CharsetUtil.UTF_8);
		if(logger.isDebugEnabled()) {
			logger.info(msgs);
		}
		messageToProxy(msgs.toString(), ctx);
	}


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		server_info.add(ctx);
		logger.info("成功与服务端建立连接。。。");
		registerConsumer();
	}

	/*** 发生异常后的处理*/
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		server_info.remove(ctx);
		cause.printStackTrace();
		ctx.close();
	}

	public static void sendMsg(MayiMqMessage msg) {
		try {
			ChannelHandlerContext ctx = getRandomServerInfo();
			ctx.writeAndFlush(JSON.toJSONString(msg)+ System.getProperty("line.separator"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送注册消费者信息
	 */
	public void registerConsumer() {

		Map<String, List<NetModel>> groupAll = GroupConsumer.getGroupAll();
		groupAll.keySet().forEach(key ->{
			MayiMqMessage mqMessage = new MayiMqMessage.Builder().key(key).transportType(MessageType.REGISTRATION_SINGLE).durable(true).build();
			try {
				getRandomServerInfo().writeAndFlush(JSON.toJSONString(mqMessage)+ System.getProperty("line.separator"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("消费者：{} 注册消息发送成功，等待服务端进行注册",key);
		});
	}

	private void messageToProxy(Object message,ChannelHandlerContext ctx) throws Exception  {
		MayiMqMessage entity = MayiMqUtils.strToEntity(message.toString());
		AchieveMethod shapeFactory = MethodFactoryProducer.getFactory().getMethod(entity.getTransportType().name());
		shapeFactory.handleMethod(entity.getKey(), message);
	}
}
