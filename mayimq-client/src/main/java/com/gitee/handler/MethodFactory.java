package com.gitee.handler;

/**
 * 方法工厂
 * @author wangchl
 *
 */
public class MethodFactory extends AbstractAchieveMethodFactory{

	@Override
	public AchieveMethod getMethod(String shape) {
		if(shape == null){
			return null;
		}  
		
		if(shape.equalsIgnoreCase("single")){
			return new LoadSingleAchieveMethod();
		} else if(shape.equalsIgnoreCase("herd")) {
			return new LoadMulticastAchieveMethod();
		}
		return null;
	}

}
