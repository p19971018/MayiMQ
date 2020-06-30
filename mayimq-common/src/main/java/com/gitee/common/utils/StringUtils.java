package com.gitee.common.utils;

public class StringUtils {

	/** ���ַ��� */
    private static final String NULLSTR = "";
    
	 /**
     * * �ж�һ�������Ƿ�Ϊ��
     * 
     * @param object Object
     * @return true��Ϊ�� false���ǿ�
     */
	public static boolean isNull(Object object)
    {
        return object == null;
    }
	
	
	 /**
     * * �ж�һ�������Ƿ�ǿ�
     * 
     * @param object Object
     * @return true���ǿ� false����
     */
    public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }
    
    /**
     * * �ж�һ���ַ����Ƿ�Ϊ�ǿմ�
     * 
     * @param str String
     * @return true���ǿմ� false���մ�
     */
    public static boolean isNotEmpty(String str)
    {
        return !isEmpty(str);
    }
    
    /**
     * * �ж�һ���ַ����Ƿ�Ϊ�մ�
     * 
     * @param str String
     * @return true��Ϊ�� false���ǿ�
     */
    public static boolean isEmpty(String str)
    {
        return isNull(str) || NULLSTR.equals(str.trim());
    }
}
