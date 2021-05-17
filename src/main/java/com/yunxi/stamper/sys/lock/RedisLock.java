package com.yunxi.stamper.sys.lock;

import com.yunxi.stamper.sys.error.exception.LockTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2020/6/19 17:43
 */
@Slf4j
@Component
public class RedisLock {

	@Autowired
	private RedissonClient redissonClient;

	private static final int timeout = 30;//超时时间
	private static final int expireTime = 15;//锁过期时间


	/**
	 * 获取分布式锁
	 *
	 * @param timeout    获取锁超时时间,超时抛出异常
	 * @param expireTime 锁定时间，超过时间自动过期，注意 @expireTime 必须大于timeout
	 * @param lockKey    锁的key
	 * @return
	 * @throws InterruptedException
	 * @throws LockTimeoutException
	 */
	public RLock tryLock(String lockKey, int timeout, int expireTime) throws InterruptedException, LockTimeoutException {
		RLock lock = redissonClient.getLock(lockKey);
		boolean isLock = lock.tryLock(timeout, expireTime, TimeUnit.SECONDS);
		if (!isLock) {
			/***未获取到锁 或 获取锁超时*/
			throw new LockTimeoutException();
		}
		return lock;
	}

	/**
	 * 获取分布式锁
	 *
	 * @param lockKey 锁的key
	 * @return
	 * @throws InterruptedException
	 * @throws LockTimeoutException
	 */
	public RLock tryLock(String lockKey) throws InterruptedException, LockTimeoutException {
		RLock lock = redissonClient.getLock(lockKey);
		boolean isLock = lock.tryLock(timeout, expireTime, TimeUnit.SECONDS);
		if (!isLock) {
			/***未获取到锁 或 获取锁超时*/
			throw new LockTimeoutException();
		}
		return lock;
	}

	/**
	 * 获取锁，拿不到就一直block，直到拿到为止(默认超时时间30s)
	 *
	 * @param lockKey
	 * @return
	 */
	public RLock lock(String lockKey) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(timeout, TimeUnit.SECONDS);
		return lock;
	}

	/**
	 * 获取锁，拿不到就一直block，直到拿到为止(默认超时时间30s)
	 *
	 * @param lockKey 锁的key
	 * @param timeout 如果是-1，直到自己解锁，否则不会自动解锁
	 * @return
	 */
	public RLock lock(String lockKey, int timeout) {
		RLock lock = redissonClient.getLock(lockKey);
		lock.lock(timeout, TimeUnit.SECONDS);
		return lock;
	}


	/**
	 * 尝试加锁，最多等待waitTime，上锁以后leaseTime自动解锁
	 *
	 * @param lockKey   锁key
	 * @param unit      锁时间单位
	 * @param waitTime  等到最大时间，强制获取锁
	 * @param leaseTime 锁失效时间
	 * @return 如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false
	 */
	public boolean fairLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
		RLock fairLock = redissonClient.getFairLock(lockKey);
		try {
			return fairLock.tryLock(waitTime, leaseTime, unit);
		} catch (InterruptedException e) {
			log.error("出现异常 ", e);
		}
		return false;
	}
}
