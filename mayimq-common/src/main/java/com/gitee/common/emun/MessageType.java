package com.gitee.common.emun;

/**
 * ��������
 * @author wangchl
 *
 */
public enum MessageType {

	/**������()*/
	SINGLE("single"),//Ĭ��
	/**Ⱥ*/
	HERD("broadcast"),
	/**��ʱ*/
	DELAY("delay"),
	
	/**������*/
	REGISTRATION_SINGLE("registeredConsumer"),
	/**Ⱥ��㲥*/
	REGISTRATION_HERD("registeredBroadcast");
	
	/** ������ */
	private String resultValue;

	
	MessageType(String resultValue) {
		this.resultValue = resultValue;
	}
	
	public String getResultValue() {
		return resultValue;
	}


	
}
