package com.gitee.aspectj.annotation.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxy<T> implements InvocationHandler{
     T target;
    
    //构造方法,给我们要代理的真实对象赋初值
    public DynamicProxy(T target) {
        this.target = target;
    }
    
    @Override
    public Object invoke(Object object, Method method, Object[] args) throws Throwable  {
        Object result = method.invoke(target, args);
        return result;
    }

}