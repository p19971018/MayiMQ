package com.gitee.common.io.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Serializable {  

	private static final String DEFAULT_ENCODING = "UTF-8";
	 
    //ÿ���̵߳� Kryo ʵ��
    private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
 
            /**
             * ��Ҫ���׸ı���������ã�����֮�����л��ĸ�ʽ�ͻᷢ���仯��
             * ���ߵ�ͬʱ�ͱ������ Redis ������л��棬
             * ������Щ�����ٻ��������л���ʱ�򣬾ͻᱨ��
             */
            //֧�ֶ���ѭ�����ã������ջ�����
            kryo.setReferences(true); //Ĭ��ֵ���� true����Ӵ��е�Ŀ����Ϊ������ά���ߣ���Ҫ�ı��������
 
            //��ǿ��Ҫ��ע���ࣨע����Ϊ�޷���֤��� JVM ��ͬһ�����ע������ͬ������ҵ��ϵͳ�д����� Class Ҳ����һһע�ᣩ
            kryo.setRegistrationRequired(false); //Ĭ��ֵ���� false����Ӵ��е�Ŀ����Ϊ������ά���ߣ���Ҫ�ı��������
//            kryo.register(Msg.class);
//            kryo.register(Msg);
 
            //Fix the NPE bug when deserializing Collections.
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                    .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
 
            return kryo;
        }
    };
 
    /**
     * ��õ�ǰ�̵߳� Kryo ʵ��
     *
     * @return ��ǰ�̵߳� Kryo ʵ��
     */
    public static Kryo getInstance() {
        return kryoLocal.get();
    }
 
    //-----------------------------------------------
    //          ���л�/�����л����󣬼�������Ϣ
    //          ���л��Ľ����������͵���Ϣ
    //          �����л�ʱ������Ҫ�ṩ����
    //-----------------------------------------------
 
    /**
     * �����󡾼����͡����л�Ϊ�ֽ�����
     *
     * @param obj �������
     * @param <T> ���������
     * @return ���л�����ֽ�����
     */
    public static <T> byte[] writeToByteArray(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
 
        Kryo kryo = getInstance();
        kryo.writeClassAndObject(output, obj);
        output.flush();
 
        return byteArrayOutputStream.toByteArray();
    }
 
    /**
     * �����󡾼����͡����л�Ϊ String
     * ������ Base64 ����
     *
     * @param obj �������
     * @param <T> ���������
     * @return ���л�����ַ���
     */
    public static <T> String writeToString(T obj) {
        try {
            return new String(Base64.encodeBase64(writeToByteArray(obj)), DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
 
    /**
     * ���ֽ����鷴���л�Ϊԭ����
     *
     * @param byteArray writeToByteArray �������л�����ֽ�����
     * @param <T>       ԭ���������
     * @return ԭ����
     */
    @SuppressWarnings("unchecked")
    public static <T> T readFromByteArray(byte[] byteArray) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Input input = new Input(byteArrayInputStream);
 
        Kryo kryo = getInstance();
        return (T) kryo.readClassAndObject(input);
    }
 
    /**
     * �� String �����л�Ϊԭ����
     * ������ Base64 ����
     *
     * @param str writeToString �������л�����ַ���
     * @param <T> ԭ���������
     * @return ԭ����
     */
    public static <T> T readFromString(String str) {
        try {
            return readFromByteArray(Base64.decodeBase64(str.getBytes(DEFAULT_ENCODING)));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
 
    //-----------------------------------------------
    //          ֻ���л�/�����л�����
    //          ���л��Ľ������������͵���Ϣ
    //-----------------------------------------------
 
    /**
     * ���������л�Ϊ�ֽ�����
     *
     * @param obj �������
     * @param <T> ���������
     * @return ���л�����ֽ�����
     */
    public static <T> byte[] writeObjectToByteArray(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
 
        Kryo kryo = getInstance();
        kryo.writeObject(output, obj);
        output.flush();
 
        return byteArrayOutputStream.toByteArray();
    }
 
    /**
     * ���������л�Ϊ String
     * ������ Base64 ����
     *
     * @param obj �������
     * @param <T> ���������
     * @return ���л�����ַ���
     */
    public static <T> String writeObjectToString(T obj) {
        try {
            return new String(Base64.encodeBase64(writeObjectToByteArray(obj)), DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
 
    /**
     * ���ֽ����鷴���л�Ϊԭ����
     *
     * @param byteArray writeToByteArray �������л�����ֽ�����
     * @param clazz     ԭ����� Class
     * @param <T>       ԭ���������
     * @return ԭ����
     */
    public static <T> T readObjectFromByteArray(byte[] byteArray, Class<T> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Input input = new Input(byteArrayInputStream);
 
        Kryo kryo = getInstance();
        return kryo.readObject(input, clazz);
    }
 
    /**
     * �� String �����л�Ϊԭ����
     * ������ Base64 ����
     *
     * @param str   writeToString �������л�����ַ���
     * @param clazz ԭ����� Class
     * @param <T>   ԭ���������
     * @return ԭ����
     */
    public static <T> T readObjectFromString(String str, Class<T> clazz) {
        try {
            return readObjectFromByteArray(Base64.decodeBase64(str.getBytes(DEFAULT_ENCODING)), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
	
}  