package com.gitee.aspectj.annotation.proxy;

/*import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;


public class DynamicProxyUtils
{
    public static  <T> Object dynamicProxy(Class<T> t,Object param) throws ClassNotFoundException {
	 

        
        //创建一个与代理对象相关联的InvocationHandler
		InvocationHandler stuHandler = new DynamicProxy<Object>(param);
        
        //创建一个代理对象stuProxy来代理，代理对象的每个执行方法都会替换执行Invocation中的invoke方法
        Object newProxyInstance = Proxy.newProxyInstance(t.getClassLoader(), new Class<?>[]{t}, stuHandler);
        return newProxyInstance;
       
    }
    
   
}*/