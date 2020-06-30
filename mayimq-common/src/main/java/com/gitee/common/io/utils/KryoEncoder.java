package com.gitee.common.io.utils;

import com.gitee.domain.MayiMqMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *  ÐòÁÐ»¯µÄHandler
 */
public class KryoEncoder  extends MessageToByteEncoder<MayiMqMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MayiMqMessage message,
                          ByteBuf out) throws Exception {
    	KyroSerializable.serialize(message, out);
        ctx.flush();
    }
}
