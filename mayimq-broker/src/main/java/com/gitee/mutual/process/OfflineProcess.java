package com.gitee.mutual.process;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.handler.ProcessMethod;
import com.gitee.mutual.domain.ConsumerInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

/**
 * 离线处理
 * @author wangchl
 *
 */
public class OfflineProcess {
	
	private final static Logger logger = LoggerFactory.getLogger(OfflineProcess.class);
	
	/**
	 * 删除消费者
	 * @param ctx
	 * @throws Exception
	 */
	public static void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		//更改标志位,让消费者进入休眠,防止消息消费者下线时消息丢失
		ProcessMethod.threadState = false;
		final ChannelId id = ctx.channel().id();
		if(logger.isInfoEnabled()) {
			logger.info("ID{},客户端:{}断开连接，正在准备下线消费者"
					+ "",id,ctx.channel().localAddress());
		}
		List<String> topic = Optional.ofNullable(ProcessMethod.getConsumerNexus(id)).get();
		for (Iterator<String> iterator = topic.iterator(); iterator.hasNext();) {
			String topics = (String) iterator.next();
			List<ConsumerInfo> handlerContexts = ProcessMethod.getHandlerContext(topics);
			for (Iterator<ConsumerInfo> consumerInfo = handlerContexts.iterator(); consumerInfo.hasNext();) {
				ConsumerInfo infos = (ConsumerInfo) consumerInfo.next();
				if(id.equals(infos.getId())) {
					consumerInfo.remove();
				}
			}
			iterator.remove();
		}
		if(logger.isInfoEnabled()) {
			logger.info("客户端断开 ,ID:{},名称 :{},IP地址为:{} 已剔除消费者队列",id,ctx.name(),ctx.channel().localAddress());
		}
		//释放消费者
		ProcessMethod.threadState = true;
	}
	
	
}
