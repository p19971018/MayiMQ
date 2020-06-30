package com.gitee.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gitee.domain.NetModel;


/**
 * 消费者信息
 * @author wangchl
 *
 */
public class GroupConsumer {

	private static Map<String,List<NetModel>> CONSUMER_GROUP = new HashMap<String,List<NetModel>>();
	
	
	public GroupConsumer() {
		
	}
	
	/**
	 * 获取全部消费者
	 * @return
	 */
	public static Map<String, List<NetModel>> getGroupAll(){
		return CONSUMER_GROUP;
	}

	/**
	 * 获取指定消费者信息
	 * @param k
	 * @return
	 */
	public static List<NetModel> getGroup(String k) {
		return CONSUMER_GROUP.get(k);
	}
	
	/**
	 * 添加消费者信息
	 * @param k
	 * @param v
	 */
	public static void addGroup(String k,List<NetModel> v) {
		 CONSUMER_GROUP.put(k, v);
	}
	
	/**
	 * 获取消费者
	 * @return
	 */
	public static Set<String> getGroupAllKey() {
		return CONSUMER_GROUP.keySet();
	}
	
	
	public static Collection<List<NetModel>> getGroupAllVal() {
		return CONSUMER_GROUP.values();
	}
	
	/**
	 * 获取随机数组
	 * @param key 消费者key
	 * @return 消费者实例
	 */
	public static NetModel getRandomGroup(String key) {
		List<NetModel> group = getGroup(key);
		return  group.get((int)Math.random() * group.size());
		
	}
	
	
	
}
