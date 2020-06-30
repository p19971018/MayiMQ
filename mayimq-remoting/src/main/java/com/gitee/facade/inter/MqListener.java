package com.gitee.facade.inter;


public interface MqListener {

	
	Object getMessage(String k);
	
	boolean existQue(String k);
}
