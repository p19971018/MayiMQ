package com.gitee.domain;

public class Proxy {

	public static void newInstance(NetModel netModel,Object parame) throws Exception {
		Class<?> forName = getClass(netModel);
		Object newInstance = forName.newInstance();
		forName.getDeclaredMethod(netModel.getMethod().toString(),Object.class).invoke(newInstance,parame);
	}
	public static void newInstance(NetModel netModel,Object... parame) throws Exception {
		Class<?> forName = getClass(netModel);
		Object newInstance = forName.newInstance();
		forName.getDeclaredMethod(netModel.getMethod().toString(),Object[].class).invoke(newInstance,new Object[] {parame});
	}
	
	private static Class<?> getClass(NetModel netModel) throws ClassNotFoundException {
		return Class.forName(netModel.getClassName().toString().replace("class", "").trim());
	}
	
}
