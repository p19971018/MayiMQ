package com.gitee.mutual.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Component;

import com.gitee.context.LoadConfigurationInfo;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * 通过database对象直接操作
 * */
@Component
public class BerkeleyDatabase<T> extends ChannelInboundHandlerAdapter implements BerkeleyDBDao<T> ,Runnable{

	private Environment env = null;
	private Database database = null;
	private Transaction txn = null;
	private DatabaseConfig databaseConfig = new DatabaseConfig();
	private EnvironmentConfig envConfig = new EnvironmentConfig();

	private String key ;
	private T msg;
	private String id;

	public static volatile String producerFileStatus;
	public static volatile String consumerFileStatus;
	
	
	public BerkeleyDatabase(String key,String id, T msg) {
		this.key = key;
		this.id = id;
		this.msg = msg;
	}

	static {
		new Thread(new mkdirFileTask()).start();
//		new Thread(new DelayMsgQueue()).start();
	}

	public BerkeleyDatabase() {
	}

	@Override
	public void openConnection(String databaseName) {
		loadEnvironment(databaseName);
		database = env.openDatabase(null, databaseName, this.databaseConfig);
	}
	

	@Override
	public void loadEnvironment(String filePath) {
		loadEnvironmentConfig();
		env = new Environment(mkdirFile(filePath+consumerFileStatus), envConfig);
		loadDatabaseConfig();
	}


	private void loadEnvironmentConfig() {
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
	}

	private void loadDatabaseConfig() {
		databaseConfig.setAllowCreate(true);
		databaseConfig.setTransactional(true);
		databaseConfig.setSortedDuplicates(true);

	}

	@Override
	public void openTransConnection(String databaseName) {
		loadEnvironment(databaseName);

		TransactionConfig config = new TransactionConfig();
		config.setReadCommitted(true);
		txn = env.beginTransaction(null, config);

		database = env.openDatabase(txn, databaseName, this.databaseConfig);
	}

	@Override
	public void closeConnection() {
		if(database != null){
			database.close();
			if(env != null){
				env.cleanLog();
				env.close();
			}
		}
	}

	@Override
	public void delete(String key) {
		DatabaseEntry keyEntry = new DatabaseEntry();
		keyEntry.setData(key.getBytes());
		OperationStatus delete = database.delete(null, keyEntry);
		System.out.println(delete.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String key) {
		T t = null;
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry valueEntry = new DatabaseEntry();
		keyEntry.setData(key.getBytes());
		if(database.get(null, keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
			ByteArrayInputStream bais = new ByteArrayInputStream(valueEntry.getData());
			try {
				ObjectInputStream ois = new ObjectInputStream(bais);
				t = (T) ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public void getKeyAllAndDel(ChannelHandlerContext ctx,String key) throws UnsupportedEncodingException {

		CursorConfig config = new CursorConfig();
		config.setReadUncommitted(true);
		T t = null;
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry valueEntry = new DatabaseEntry();
		//		keyEntry.setData(key.getBytes());

		TransactionConfig trconfig = new TransactionConfig();
		config.setReadUncommitted(true);
		Transaction txn = env.beginTransaction(null, trconfig);

		Cursor cursor2 = database.openCursor(txn, config);

		//		OperationStatus result2 = cursor2.getSearchKey(keyEntry, valueEntry, null);
		OperationStatus result2 = cursor2.getFirst(keyEntry, valueEntry, LockMode.DEFAULT);

		while (result2 == OperationStatus.SUCCESS) {
			ByteArrayInputStream bais = new ByteArrayInputStream(valueEntry.getData());
			try {
				ObjectInputStream ois = new ObjectInputStream(bais);
				t = (T) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				cursor2.delete();
				ctx.channel().writeAndFlush(t.toString() + System.getProperty("line.separator"));
			} catch (Exception e) {
				//事务冲突，关闭重新打开
				return;
			}
			

			result2 = cursor2.getNext(keyEntry, valueEntry, null);
		}
		result2 = cursor2.getNext(keyEntry, valueEntry, null);
	}

	@Override
	public void update(String key, T t) {
		save(key, t);
	}


	@Override
	public void save(String key, T value) {
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry valueEntry = new DatabaseEntry();
		keyEntry.setData(key.getBytes());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		valueEntry.setData(baos.toByteArray());

		database.put(null, keyEntry, valueEntry);

	}

	@Override
	public void save(String key,String id, T msg,String val) {
		new Thread(new BerkeleyDatabase<>(key,id,msg)).start();
	}



	@Override
	public void run() {
		loadEnvironmentConfig();
		env = new Environment(mkdirFile(key+producerFileStatus), envConfig);
		loadDatabaseConfig();

		TransactionConfig config = new TransactionConfig();
		config.setReadCommitted(true);
		txn = env.beginTransaction(null, config);

		database = env.openDatabase(txn, this.key, databaseConfig);
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry valueEntry = new DatabaseEntry();
		keyEntry.setData(id.getBytes());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		valueEntry.setData(baos.toByteArray());
		database.putNoDupData(null, keyEntry, valueEntry);
	}
	
	private File mkdirFile(String filePath) {
		File dirPath = new File(LoadConfigurationInfo.getDbPath()+filePath);
		if(!dirPath.exists()) {
			dirPath.mkdirs();
			dirPath.mkdir();
		}
		return dirPath;
	}
}

class  mkdirFileTask implements Runnable {
	GregorianCalendar calendar = new GregorianCalendar();
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		while(true) {
			Date date = new Date();
			calendar.setTime(date);
			if (calendar.get(Calendar.MINUTE) == 30 || calendar.get(Calendar.MINUTE) ==0 ) {
				String time = String.valueOf(Calendar.DATE)+String.valueOf(Calendar.HOUR_OF_DAY)+String.valueOf(Calendar.MINUTE);
				
				BerkeleyDatabase.producerFileStatus =time;
				try {
					Thread.currentThread().sleep(8000);
					BerkeleyDatabase.consumerFileStatus =time;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
