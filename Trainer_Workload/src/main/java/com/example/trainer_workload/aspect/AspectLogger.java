package com.example.trainer_workload.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class AspectLogger {
    private static final Logger transactionLogger = LoggerFactory.getLogger("transaction");
    private static final Logger operationLogger = LoggerFactory.getLogger("operation");

    @Around("execution(* com.example.trainer_workload.service.TrainerServiceImpl.handleTrainerWorkload(..))")
    public Object logTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String transactionId = UUID.randomUUID().toString();
        transactionLogger.info("Transaction {}: Starting {}", transactionId, joinPoint.getSignature());

        try {
            Object result = joinPoint.proceed();
            transactionLogger.info("Transaction {}: Completed {}", transactionId, joinPoint.getSignature());
            return result;
        } catch (Throwable throwable) {
            transactionLogger.error("Transaction {}: Error in {}", transactionId, joinPoint.getSignature(), throwable);
            throw throwable;
        }
    }

    @Before("execution(* com.example.trainer_workload.service.TrainerServiceImpl.*(..)) && !execution(* com.example.trainer_workload.service.TrainerServiceImpl.handleTrainerWorkload(..))")
    public void logOperationStart(JoinPoint joinPoint) {
        operationLogger.info("Starting {}", joinPoint.getSignature());
    }

    @AfterReturning("execution(* com.example.trainer_workload.service.TrainerServiceImpl.*(..)) && !execution(* com.example.trainer_workload.service.TrainerServiceImpl.handleTrainerWorkload(..))")
    public void logOperationEnd(JoinPoint joinPoint) {
        operationLogger.info("Completed {}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* com.example.trainer_workload.service.TrainerServiceImpl.*(..)) && !execution(* com.example.trainer_workload.service.TrainerServiceImpl.handleTrainerWorkload(..))", throwing = "exception")
    public void logOperationError(JoinPoint joinPoint, Throwable exception) {
        operationLogger.error("Error in {}", joinPoint.getSignature(), exception);
    }
}
