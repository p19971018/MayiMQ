package com.gitee.handler;

import org.springframework.stereotype.Component;

import com.gitee.client.GroupConsumer;
import com.gitee.common.io.utils.MayiMqUtils;
import com.gitee.domain.NetModel;
import com.gitee.domain.Proxy;

/**
 * 默认消息处理
 * @author wangchl
 *
 */
@Component
public class LoadSingleAchieveMethod implements AchieveMethod{
	

	@Override
	public void handleMethod(String key,Object param) throws Exception {
		NetModel randomGroup = GroupConsumer.getRandomGroup(key);
		Proxy.newInstance(randomGroup, MayiMqUtils.strToEntity(param.toString()).getValue());
		
	}


}
