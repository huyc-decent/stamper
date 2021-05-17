package com.yunxi.stamper.sys.aop;

import com.yunxi.stamper.sys.lock.RedisLock;
import com.zengtengpeng.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/6/24 9:18
 */
@Slf4j
@Aspect
@Component
public class RedissonLockAop {

	@Autowired
	private RedisLock redisLock;

	/**
	 * 定义切入点：对要拦截的方法进行定义与限制，如包、类
	 */
	@Pointcut("execution(* com.yunxi.stamper.service.impl..*.add*(..)) " +
			"|| execution(* com.yunxi.stamper.service.impl..*.update* (..)) " +
			"|| execution(* com.yunxi.stamper.service.impl..*.delete* (..)) " +
			"|| execution(* com.yunxi.stamper.service.impl..*.del*(..)) ")
	private void cutMethod() {
	}

	/**
	 * 前置通知：在目标方法执行前调用
	 */
	@Before("cutMethod()")
	public void begin() {
	}

	/**
	 * 后置通知：在目标方法执行后调用，若目标方法出现异常，则不执行
	 */
	@AfterReturning("cutMethod()")
	public void afterReturning() {
	}

	/**
	 * 后置/最终通知：无论目标方法在执行过程中出现异常都会在它之后调用
	 */
	@After("cutMethod()")
	public void after() {
	}

	/**
	 * 环绕通知：灵活自由的在目标方法中切入代码
	 */
	@Around("cutMethod()")
	public void around(ProceedingJoinPoint joinPoint) throws Throwable {

		/***如果目标已有lock注解，则不需要手动加锁了*/
		if (hasLockAnnotaion(joinPoint)) {
			joinPoint.proceed();
			return;
		}

		/**
		 * 以目标 "类名_方法名" 加锁
		 */
		String methodName = joinPoint.getSignature().getName();
		String name = joinPoint.getTarget().getClass().getName();
		String lockKey = name + "_" + methodName;
		RLock lock = redisLock.tryLock(lockKey);
		try {
			log.debug(lockKey + "\t已加锁");
			joinPoint.proceed();
		} finally {
			if (lock != null) {
				try {
					lock.unlock();
				} catch (Exception e) {
					log.error("解锁异常", e);
				}
			}
		}
	}

	/**
	 * 目标方法是否含有 @lock 注解
	 *
	 * @param joinPoint
	 * @return true:已存在 false:不存在
	 * @throws NoSuchMethodException
	 */
	private boolean hasLockAnnotaion(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
		String methodName = joinPoint.getSignature().getName();
		Class[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();

		Object target = joinPoint.getTarget();
		Method method = target.getClass().getMethod(methodName, parameterTypes);
		Lock lock = method.getAnnotation(Lock.class);
		return lock != null;
	}


	/**
	 * 异常通知：目标方法抛出异常时执行
	 */
	@AfterThrowing("cutMethod()")
	public void afterThrowing() {
	}
}
