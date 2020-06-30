package com.gitee.context.pattern;

public class NullCustomer extends AbstractCustomer{

	@Override
	public boolean isNil() {
		return true;
	}

	@Override
	public String getName() {
		return "Object is empty";
	}

	
}
