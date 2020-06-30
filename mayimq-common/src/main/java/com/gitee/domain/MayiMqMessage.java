package com.gitee.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.gitee.common.emun.MessageType;


public class MayiMqMessage  implements Serializable,Delayed{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private long addTime; 		    //添加时间
	private MessageType transportType; //发送类型
	private String key;			    //主键
	private Object value;		    //数据
	private int retryCount;         //重试次数
	private int status;			    //状态
	private int receipt;            //回执
	private String  multicastAddr;  //广播地址
	private boolean durable ;       //是否持久化
	private long delayTime; 	    //延迟时间

	
	public MayiMqMessage(Builder builder) {
		this.key = builder.key;
		this.transportType = builder.transportType;
		this.id = builder.id;
		this.addTime = builder.addTime;
		this.value = builder.value;
		this.retryCount = builder.retryCount;
		this.status = builder.status;
		this.receipt = builder.receipt;
		this.multicastAddr = builder.multicastAddr;
		this.durable = builder.durable;
		this.delayTime = builder.delayTime;
	}
	
	
	public static final class Builder{
		private String id;
		private long addTime; //方法名
		private MessageType transportType; //发送类型
		private String key;			    //主键
		private Object value;		    //数据
		private int retryCount;         //重试次数
		private int status;			    //状态
		private int receipt;            //回执
		private String  multicastAddr;  //广播地址
		private boolean durable ;       //是否持久化
		private long delayTime; 	    //延迟时间

		public Builder() {
		}

		public Builder addTime(long addTime) { 
			this.addTime = addTime; 
			return this; 
		} 
		public Builder id(String id) { 
			this.id = id; 
			return this; 
		} 
		public Builder transportType(MessageType transportType) { 
			this.transportType = transportType; 
			return this; 
		} 
		public Builder key(String key) { 
			this.key = key; 
			return this; 
		} 

		public Builder value(Object value) { 
			this.value = value; 
			return this; 
		} 
		public Builder retryCount(int retryCount) { 
			this.retryCount = retryCount; 
			return this; 
		} 
		
		public Builder status(int status) { 
			this.status = status; 
			return this; 
		} 
		public Builder receipt(int receipt) { 
			this.receipt = receipt; 
			return this; 
		} 
		public Builder multicastAddr(String multicastAddr) { 
			this.multicastAddr = multicastAddr; 
			return this; 
		} 
		
		public Builder durable(boolean durable) { 
			this.durable = durable; 
			return this; 
		} 
		public Builder delayTime(long delayTime) { 
			this.delayTime = delayTime; 
			return this; 
		} 
		public MayiMqMessage build() {
			return new  MayiMqMessage(this);} 
	}

	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public MayiMqMessage(MessageType transportType, Object value) {
		this.transportType = transportType;
		this.value = value;
	}

	public String getMulticastAddr() {
		return multicastAddr;
	}

	public void setMulticastAddr(String multicastAddr) {
		this.multicastAddr = multicastAddr;
	}

	

	public String getId() {
		return id;
	}

	public void setId() {
		this.id =UUID.randomUUID().toString();
	}
	public void setId(String id) {
		this.id =id;
	}

	public MessageType getTransportType() {
		return transportType;
	}

	public int getReceipt() {
		return receipt;
	}

	public void setReceipt(int receipt) {
		this.receipt = receipt;
	}

	public void setTransportType(MessageType transportType) {
		this.transportType = transportType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getAddTime() {
		return new Date().getTime();
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	@Override
	public String toString() {
		return "MayiMqMessage [id=" + id + ", addTime=" + addTime + ", transportType=" + transportType + ", key=" + key
				+ ", value=" + value + ", retryCount=" + retryCount + ", status=" + status + ", receipt=" + receipt
				+ ", multicastAddr=" + multicastAddr + ", durable=" + durable + ", delayTime=" + delayTime
				;
	}

	public MayiMqMessage() {
	}

	//到期时间,但传入的数值代表过期的时长，传入单位毫秒
	private long activeTime;
	private MayiMqMessage data;//业务数据，泛型

	//传入过期时长,单位秒，内部转换
	public MayiMqMessage(long expirationTime, MayiMqMessage data) {
		this.activeTime = expirationTime+System.currentTimeMillis();
		this.data = data;
	}

	

	public long getActiveTime() {
		return activeTime;
	}

	public MayiMqMessage getData() {
		return data;
	}
	


	/*
	 * 这个方法返回到激活日期的剩余时间，时间单位由单位参数指定。
	 */
	public long getDelay(TimeUnit unit) {
		return unit.convert(this.activeTime -System.currentTimeMillis(),unit);
	}

	/*
	 *Delayed接口继承了Comparable接口，按剩余时间排序，实际计算考虑精度为纳秒数
	 */
	public int compareTo(Delayed o) {
		long d = (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
		if (d==0){
			return 0;
		}else{
			if (d<0){
				return -1;
			}else{
				return  1;
			}
		}
	}

}
