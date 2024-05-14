package com.example.task_hibernate.aspect;

import com.example.task_hibernate.util.IdentityHolder;
import jakarta.xml.bind.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j(topic = "info")
@Component
public class AspectLogger {

    @Pointcut("within(com.example.task_hibernate.repository.*)")
    public void daoClasses() {
    }

    @Pointcut("within(com.example.task_hibernate.service.*)")
    public void serviceClasses() {
    }

    @Pointcut("within(com.example.task_hibernate.controller.*)")
    public void controllerClasses() {
    }

    @Around("daoClasses() || serviceClasses() || controllerClasses()")
    public Object proceedLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;

        try {
            String requestIdentity = IdentityHolder.getIdentity();
            String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();

            long startTimeMillis = System.currentTimeMillis();
            log.info("{} - Start REST Call: {}.{}({})", requestIdentity, className, methodName, Arrays.toString(args));

            proceed = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTimeMillis;
            log.info("{} - {}.{} finished REST Call by: {} ms. With result: {}", requestIdentity, className, methodName, executionTime, proceed == null ? "null" : proceed.toString());
        } catch (DataAccessException ex) {
            log.error("Database access error occurred: {}", ex.getMessage());
            throw ex;
        } catch (ValidationException ex) {
            log.error("Validation error occurred: {}", ex.getMessage());
            throw ex;
        } catch (Throwable throwable) {
            log.error("Throwable occurred: {}", throwable.getMessage(), throwable);
            throw throwable;
        }
        finally {
            IdentityHolder.clearIdentity();
        }
        return proceed;
    }

}
