package com.gitee.mutual.process;

import com.gitee.common.emun.MessageType;
import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.context.pattern.AbstractCustomer;
import com.gitee.context.pattern.RealCustomer;
import com.gitee.domain.CheckQueue;
import com.gitee.domain.MayiMqMessage;
import com.gitee.domain.NetModel;
import com.gitee.domain.Proxy;
import com.gitee.handler.ProcessMethod;

import io.netty.channel.ChannelHandlerContext;

public class MessageProcess {

	private Object message;
	private ChannelHandlerContext ctx;
	
	public MessageProcess(ChannelHandlerContext ctx , Object message ) {
		this.message = message;
		this.ctx = ctx;
	}
	
	
	public AbstractCustomer messageToProxy() throws Exception {
		CheckQueue.message(message);
		return messageToProxy(ctx,message);
	}
	
	/**
	 * 消息代理
	 * @param ctx
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private  AbstractCustomer messageToProxy(ChannelHandlerContext ctx,Object message) throws Exception {
		MayiMqMessage entity = MayiMqUtils.strToEntity(message.toString());
		NetModel loadProcess = loadProcess(entity.getTransportType());
		Proxy.newInstance(loadProcess, message, ctx);
		return new RealCustomer(loadProcess.toString());

	}
	
	public static NetModel loadProcess(MessageType type) {
		return new NetModel.Builder()
		 .className(ProcessMethod.class).method(type.getResultValue()).build();
		
	}
}
