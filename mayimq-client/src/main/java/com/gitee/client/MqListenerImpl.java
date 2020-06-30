package com.gitee.client;

import org.springframework.stereotype.Service;

import com.gitee.facade.inter.MqListener;
@Service
public class MqListenerImpl implements MqListener{

	@Override
	public Object getMessage(String k) {
		System.out.println("MqListenerImpl已执行"+k);
		return null;
	}

	@Override
	public boolean existQue(String k) {
		// TODO Auto-generated method stub
		return false;
	}

}
