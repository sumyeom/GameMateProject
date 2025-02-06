package com.example.gamemate.global.common.aop;

import com.example.gamemate.domain.user.entity.User;
import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private final RedissonClient redissonClient;
    private static final String LOCK_PREFIX = "LOCK:";

    @Around("@annotation(distributedLock)")
    public Object executeWithLock(
            ProceedingJoinPoint joinPoint,
            DistributedLock distributedLock
    ) throws Throwable {
        String lockKey = LOCK_PREFIX + "coupon:" + joinPoint.getArgs()[0];
        RLock lock = redissonClient.getLock(lockKey);

        log.info("락 획득 시도: {}", lockKey);
        boolean isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

        if (!isLocked) {
            log.warn("락 획득 실패: {}", lockKey);
            throw new ApiException(ErrorCode.COUPON_ISSUE_FAILED);
        }

        log.info("락 획득 완료: {}", lockKey);

        try {
            Object result = joinPoint.proceed();

            // 트랜잭션이 완료된 후 락 해제
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("락 해제 완료: {}", lockKey);
                    }
                }
            });

            return result;
        } catch (Exception e) {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            throw e;
        }
    }
}