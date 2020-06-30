package com.gitee.client;


/*公共网络通信类，通过序列化该类,将客户端调用接口、方法、参数、参数类型封装，然后服务端反序列化,再通过反射,调取相应实现类的方法。*/
public class NetModel {


	
	private Object className; //接口名

	private Object method; //方法名


	public Object getClassName() {
		return className;
	}

	public Object getMethod() {
		return method;
	}

	public NetModel(Builder builder) {
		this.className = builder.className;
		this.method = builder.method;
	}

	public static final class Builder{
		private Object className;

		private Object method; //方法名

		public Builder() {
		}

		public Builder className(Object clazz) { 
			className = clazz; 
			return this; 
		} 
		public Builder method(Object val) { 
			method = val; 
			return this; 
		} 

		public NetModel build() {
			return new  NetModel(this);} 
	}

	
}





