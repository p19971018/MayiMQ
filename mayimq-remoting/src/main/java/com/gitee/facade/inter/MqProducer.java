package com.gitee.facade.inter;

public interface MqProducer {
	
	void setQueue(String k, Object v) throws InterruptedException;

}
