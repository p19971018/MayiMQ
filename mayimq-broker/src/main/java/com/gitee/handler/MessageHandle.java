package com.gitee.handler;

import java.io.IOException;

public interface MessageHandle {

	
	public void delay(Object[] message);

	public void single(Object[] message) throws InterruptedException;


	public void broadcast(Object[] message) throws IOException ;
	
	public void registeredBroadcast(Object[] key);

}
