package com.gitee.mutual.database;

public interface BerkeleyDBDao<T> {
	/**
	 * open database
	 * @param filePath 数据库存储路径
	 * @param dbName 数据库名称
	 * */
	public void openConnection(String dbName);
	
	public void openTransConnection(String databaseName);
	/**
	 * 加载配置
	 * @param filePath
	 */
	public void loadEnvironment(String filePath);
	/**
	 * 关闭数据库
	 * */
	public void closeConnection();
	/**
	 * insert
	 * */
	public void save(String key, T value);

	/**
	 * 线程使用方法
	 * @param key 主键
	 * @param t 参数
	 * @param filePath 路径
	 * @param databaseName 数据库名
	 */
	public void save(String key,String id, T msg,String val);
	/**
	 * delete
	 * */
	public void delete(String key);
	/**
	 * update
	 * */
	public void update(String key, T value);
	/**
	 * select one
	 * */
	public T get(String key);

	
}