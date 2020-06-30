package com.gitee.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class LoadConfigurationInfo {

	protected static final int port;
	protected static final String db_path;
	protected static final int broadcast_port ;
	protected static final String startNetwork;

	
	private LoadConfigurationInfo() {
	}
	static {
		Properties properties = new Properties();
		InputStream inputStream = Object.class.getResourceAsStream("/MayiMq.properties");
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		db_path = properties.get("db.storage.path").toString();
		port = Integer.parseInt(properties.get("mayiMq.port").toString());
		broadcast_port = Integer.parseInt(properties.get("mayiMq.broadcast.port").toString());
		startNetwork = properties.get("mayiMq.broadcast.startNetwork").toString();
	}

	public static int getPort() {
		return port;
	}
	public static String getDbPath() {
		return db_path;
	}
	public static int getBroadcastPort() {
		return broadcast_port;
	}
	public static String getStartNetwork() {
		return startNetwork;
	}

}
