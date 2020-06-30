package com.gitee.domain;
/*��������ͨ���࣬ͨ�����л�����,���ͻ��˵��ýӿڡ��������������������ͷ�װ��Ȼ�����˷����л�,��ͨ������,��ȡ��Ӧʵ����ķ�����*/
public class NetModel {


	
	private Object className; //�ӿ���

	private Object method; //������


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

		private Object method; //������

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





