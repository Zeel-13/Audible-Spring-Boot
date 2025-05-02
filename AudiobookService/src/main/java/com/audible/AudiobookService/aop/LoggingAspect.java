package com.audible.AudiobookService.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all service layer methods
    @Pointcut("execution(* com.audible.AudiobookService.service.*.*(..))")
    public void serviceLayerPointcut() {}


    // Log before a service method is executed
    @Before("serviceLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("🔍 [BEFORE] Method called: {} | Args: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // Log after a service method returns successfully
    @AfterReturning(pointcut = "serviceLayerPointcut()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("✅ [AFTER] Method completed: {} | Result: {}",
                joinPoint.getSignature().getName(), result);
    }

    // Log if a service method throws an exception
    @AfterThrowing(pointcut = "serviceLayerPointcut()", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Exception ex) {
        logger.error("❌ [SERVICE EXCEPTION] in method: {} | Exception: {}",
                joinPoint.getSignature().getName(), ex.getMessage());
    }

    
}