package com.gitee.common.io.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KyroSerializable {

	public static void main(String[] args) throws IOException {  
		long start =  System.currentTimeMillis();  
		//        setSerializableObject();  
		System.out.println("Kryo 序列化时间:" + (System.currentTimeMillis() - start) + " ms" );  
		start =  System.currentTimeMillis();  
		//        getSerializableObject();  
		System.out.println("Kryo 反序列化时间:" + (System.currentTimeMillis() - start) + " ms");  

	}  

	private static Kryo kryo = KryoFactory.createKryo();

	/*序列化方法*/
	public static void serialize(Object object, ByteBuf out) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output = new Output(baos);
		kryo.writeClassAndObject(output, object);
		output.flush();
		output.close();

		byte[] b = baos.toByteArray();
		try {
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.writeBytes(b);
	}

	
	/*反序列化方法*/
	public static Object deserialize(ByteBuf out) {
		if (out == null) {
			return null;
		}
		Input input = new Input(new ByteBufInputStream(out));
		return kryo.readClassAndObject(input);
	}
}
