package com.gitee.handler;

/**
 * 加载处理方法
 * @author Administrator
 *
 */
//@Lazy
//@Component
public class LoadProcessMethod {
	
	/*static Map<MessageType,NetModel> HandlerMap = new HashMap<>();
	
	static Map<MessageType,NetModel> RegisteredMap = new HashMap<>();
	
	static {
		HandlerMap.put(MessageType.SINGLE, loadSingle());
		HandlerMap.put(MessageType.DELAY, loadDelay());
		HandlerMap.put(MessageType.HERD, loadBroadcast());
		
		RegisteredMap.put(MessageType.REGISTRATION_HERD, loadRegisteredBroadcast());
		RegisteredMap.put(MessageType.REGISTRATION_SINGLE, loadRegisteredConsumer());
		
	}
*/

	/*public static NetModel loadProcess(MessageType type) {
		return new NetModel.Builder()
		 .className(ProcessMethod.class).method(type.getResultValue()).build();
		
	}*/
	
	/*private static NetModel loadSingle() {
		return new NetModel.Builder()
				.className(ProcessMethod.class)
				.method("single").build();
	}
	
	
	public static NetModel loadDelay() {
		return new NetModel.Builder()
				.className(ProcessMethod.class)
				.method("delay").build();
	}
	
	public static NetModel loadBroadcast() {
		return new NetModel.Builder()
				.className(ProcessMethod.class)
				.method("broadcast").build();
	}
	
	public static NetModel loadRegisteredBroadcast() {
		return new NetModel.Builder()
				.className(ProcessMethod.class)
				.method("registeredBroadcast").build();
	}
	
	public static NetModel loadRegisteredConsumer() {
		return new NetModel.Builder()
				.className(ProcessMethod.class)
				.method("registeredConsumer").build();
	}
	

	public static NetModel getKeyNetModel(MessageType messageType){
		 NetModel netModel = HandlerMap.get(messageType);
		 if(null == netModel) {
			 netModel = RegisteredMap.get(messageType);
		 }
		return netModel;
	}
		
	public static NetModel getNetModel(MessageType messageType){
		return HandlerMap.get(messageType);
	}
	
	public static NetModel getNetModelRegistered(MessageType messageType){
		return RegisteredMap.get(messageType);
	}*/

}