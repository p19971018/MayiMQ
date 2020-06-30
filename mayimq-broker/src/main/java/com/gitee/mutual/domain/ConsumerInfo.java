package com.gitee.mutual.domain;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

public class ConsumerInfo {

	private ChannelId id;
	private ChannelHandlerContext handlerContext;




	public ConsumerInfo(Builder builder) {
		this.id = builder.id;
		this.handlerContext = builder.handlerContext;
	}



	public ConsumerInfo() {

	}



	public static final class Builder {

		private ChannelId id;
		private ChannelHandlerContext handlerContext;

		public Builder() {
		}

		public Builder id(ChannelId id ) {
			this.id = id;
			return this;

		}
		public Builder handlerContext(ChannelHandlerContext handlerContext ) {
			this.handlerContext = handlerContext;
			return this;

		}

		public ConsumerInfo build() {
			return new ConsumerInfo(this);
		}
	}



	public ChannelId getId() {
		return id;
	}



	public void setId(ChannelId id) {
		this.id = id;
	}



	public ChannelHandlerContext getHandlerContext() {
		return handlerContext;
	}



	public void setHandlerContext(ChannelHandlerContext handlerContext) {
		this.handlerContext = handlerContext;
	}



	@Override
	public String toString() {
		return "ConsumerInfo [id=" + id + ", handlerContext=" + handlerContext + "]";
	}


}
