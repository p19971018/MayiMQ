package com.gitee.common.emun;

/**
 * 发送类型
 * @author wangchl
 *
 */
public enum MessageType {

	/**生产者()*/
	SINGLE("single"),//默认
	/**群*/
	HERD("broadcast"),
	/**延时*/
	DELAY("delay"),
	
	/**单消费*/
	REGISTRATION_SINGLE("registeredConsumer"),
	/**群组广播*/
	REGISTRATION_HERD("registeredBroadcast");
	
	/** 错误码 */
	private String resultValue;

	
	MessageType(String resultValue) {
		this.resultValue = resultValue;
	}
	
	public String getResultValue() {
		return resultValue;
	}


	
}
