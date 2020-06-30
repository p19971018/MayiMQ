package com.gitee.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.gitee.client.GroupConsumer;
import com.gitee.domain.NetModel;
import com.gitee.mq.annotation.IMqConsumer;

@Component
public class ListenerProcessor implements BeanPostProcessor {

    /**
     * 获取消费者
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods != null) {
            for (Method method : methods) {
                IMqConsumer permissionOperation = AnnotationUtils.findAnnotation(method, IMqConsumer.class);
                if (null != permissionOperation) {
                	
                	List<NetModel> group = GroupConsumer.getGroup(permissionOperation.value());
                	if(null != group) {
                		group.add(new NetModel.Builder()
                    			.className(method.getDeclaringClass())
                    			.method(method.getName())
                    			.build());
                		GroupConsumer.addGroup(permissionOperation.value(), group);
                	}else {
                		List<NetModel> model = new ArrayList<>();
                		model.add(new NetModel.Builder()
                    			.className(method.getDeclaringClass())
                    			.method(method.getName())
                    			.build());
                		GroupConsumer.addGroup(permissionOperation.value(), model);
                	}
               }
            }
        }
        return bean;
    }
   
    
}