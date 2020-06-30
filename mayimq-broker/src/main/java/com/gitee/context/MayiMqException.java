package com.gitee.context;

/**
 * 错误提示类
 * @author wangchul
 *
 */
public class MayiMqException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
    public MayiMqException() {
        super();
    }

    public MayiMqException(String message) {
        super(message);
    }
    

    public MayiMqException(String message, Throwable cause) {
        super(message, cause);
    }

    public MayiMqException(Throwable cause) {
        super(cause);
    }

    protected MayiMqException(String error, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(error, cause, enableSuppression, writableStackTrace);
    }
    

}
