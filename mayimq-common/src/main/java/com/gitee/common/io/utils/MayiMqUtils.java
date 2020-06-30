package com.gitee.common.io.utils;

import com.alibaba.fastjson.JSON;
import com.gitee.domain.MayiMqMessage;

public class MayiMqUtils {

	public static MayiMqMessage strToEntity(String parame) {
		return JSON.parseObject(parame,MayiMqMessage.class);
	}
	
	public static MayiMqMessage byteToEntity(byte[] parame) {
		return JSON.parseObject(parame,MayiMqMessage.class);
	}
}
