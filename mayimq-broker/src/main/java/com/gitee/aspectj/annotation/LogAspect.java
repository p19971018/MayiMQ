package com.gitee.aspectj.annotation;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gitee.aspectj.annotation.lang.Log;
import com.gitee.aspectj.annotation.lang.domain.OperLog;
import com.gitee.common.utils.StringUtils;

@Aspect
@Component
public class LogAspect  {
	

	private final static Logger logger = LoggerFactory.getLogger(LogAspect.class);
	
	
	@Pointcut(value = "@annotation(com.gitee.aspectj.annotation.lang.Log)")
	public void logPointCut() {
		
	}
	
	@AfterReturning(pointcut = "logPointCut()")
	private void doBefore(JoinPoint joinPoint) {
		 handleLog(joinPoint, null);

	}
	@AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e)
    {
        handleLog(joinPoint, e);
    }
	
	protected void handleLog(final JoinPoint joinPoint,final Exception e) {
		
		try {
			Log annotationLog = getAnnotationLog(joinPoint);
			if(StringUtils.isNull(annotationLog)) {
				return ;
			}
			
			OperLog operLog= new OperLog();
			operLog.setOperId(new Date().getTime());
			getControllerMethodDescription(annotationLog,operLog);
			if(logger.isInfoEnabled()) {
				logger.info(MessageFormat.format("{0}收到消息,consumerTag:[{1}]", operLog.getTitle(),operLog.getOperatingType()));
			}
		} catch (Exception exp) {
			// 记录本地异常日志
			logger.error("==前置通知异常==");
			logger.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
		}

	}
	
	private void getControllerMethodDescription(Log log,OperLog operLog) {
		operLog.setOperatingType(log.operatingType().ordinal());
		operLog.setTitle(log.title());
	}
	
	
	private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
		
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature)signature;
		Method method = methodSignature.getMethod();
		
		if(method != null) {
			return method.getAnnotation(Log.class);
		}
		return null;

	}
	
	
}
