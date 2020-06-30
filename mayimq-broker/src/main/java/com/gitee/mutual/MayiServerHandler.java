package com.gitee.mutual;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.handler.ProcessMethod;
import com.gitee.mutual.domain.ConsumerInfo;
import com.gitee.mutual.process.MessageProcess;
import com.gitee.mutual.process.OfflineProcess;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息核心处理类
 * @author wangchl
 *
 */
@ChannelHandler.Sharable
public class MayiServerHandler extends ProcessMethod {

	private final static Logger logger = LoggerFactory.getLogger(MayiServerHandler.class);

	/**
	 * 消息核心处理类
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		new MessageProcess(ctx,msg).messageToProxy();
	}



	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ConcurrentHashMap<String, List<ConsumerInfo>> consumerNexus = ProcessMethod.getConsumerNexus();
		System.out.println(consumerNexus.toString());
		if(logger.isInfoEnabled()) {
			logger.info("发生注册事件 ,ID:{}, 名称: {},IP地址为 :{}",ctx.channel().id(),ctx.name(),ctx.channel().localAddress());
		}
		ctx.fireChannelRegistered();
	}


	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		OfflineProcess.channelUnregistered(ctx);
		ctx.fireChannelUnregistered();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("新客户端连接接入。。。 ,ID:{},名称 :{},IP地址为 :{}",ctx.channel().id(),ctx.name(),ctx.channel().localAddress());
		}
		ctx.fireChannelActive();
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("发现有客户端失去连接。。。 ,ID : {},名称 : {},IP地址为:  {}",ctx.channel().id(),ctx.name(),ctx.channel().localAddress());
		}
		ctx.fireChannelInactive();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	public static List<ConsumerInfo> getHandlerContext(Object key) {
		if(null == key) {
			throw new NullPointerException(" key is not null   ");
		}
		return ProcessMethod.getHandlerContext(key);

	}

	

}
