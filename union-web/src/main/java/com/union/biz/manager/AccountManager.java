package com.union.biz.manager;


import com.union.base.exception.BaseException;
import com.union.biz.mapper.AccountLogMapper;
import com.union.biz.mapper.AccountMapper;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.model.UnionAccountLogDO;
import com.union.constant.CacheConstant;
import com.union.enums.AccountTargetTypeEnum;
import com.union.event.UnionAccountUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AccountManager {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AccountLogMapper accountLogMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ApplicationContext applicationContext;

    @Transactional(rollbackFor = Exception.class)
    public void income(Long userId, BigDecimal tradeAmount,
                       Long targetId,
                       AccountTargetTypeEnum targetType, String summary) {
        //判断操作金额必须大于0
        if (tradeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseException( "请输入正确金额");
        }
        String lockKey = String.format(CacheConstant.USER_ACCOUNT_KEY, userId);
        RLock rLock = redissonClient.getLock(lockKey);

        boolean tryLock;
        try {
            tryLock = rLock.tryLock(30, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BaseException( "请勿重复提交");
        }
        if (!tryLock) {
            throw new BaseException( "请勿重复提交");
        }
        try {
            UnionAccountDO account = accountMapper.findFirstByUserId(userId);
            if (account == null) {
                account = initAccount(userId);
            }
            //日志，操作前资金
            UnionAccountLogDO log1 = new UnionAccountLogDO();
            log1.setTotalOld(account.getTotal());
            log1.setBalanceOld(account.getBalance());
            log1.setIncomeOld(account.getIncome());
            log1.setIncomeNew(account.getIncome().add(tradeAmount));
            log1.setTotal(account.getTotal().add(tradeAmount));
            log1.setBalanceNew(account.getBalance().add(tradeAmount));
            log1.setAccountId(account.getId());
            log1.setLogType("INCOME");
            log1.setAmount(tradeAmount);
            log1.setIncome(tradeAmount);
            log1.setBalance(tradeAmount);
            log1.setTargetId(targetId);
            log1.setTargetCode("");
            log1.setUserId(userId);
            log1.setTargetName(targetType.getMessage());
            log1.setTargetType(targetType.name());
            log1.setSummary(summary);
            log1.setExpend(BigDecimal.ZERO);
            log1.setExpendNew(account.getExpend());
            log1.setExpendOld(account.getExpend());
            log1.setFrost(BigDecimal.ZERO);
            log1.setFrostNew(account.getFrost());
            log1.setFrostOld(account.getFrost());
            int i = accountLogMapper.insert(log1);
            if (i < 1) {
                throw new BaseException( "插入失败");
            }

            account.setTotal(account.getTotal().add(tradeAmount));
            account.setIncome(account.getIncome().add(tradeAmount));
            account.setBalance(account.getBalance().add(tradeAmount));
            i = accountMapper.updateById(account);
            if (i < 1) {
                throw new BaseException( "更新失败");
            }
            applicationContext.publishEvent(new UnionAccountUpdateEvent(applicationContext, account, tradeAmount, targetType));
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void expend(Long userId, BigDecimal tradeAmount,
                       Long targetId,
                       AccountTargetTypeEnum targetType, String summary) {
        //判断操作金额必须大于0
        if (tradeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseException( "请输入正确金额");
        }
        String lockKey = String.format(CacheConstant.USER_ACCOUNT_KEY, userId);
        RLock rLock = redissonClient.getLock(lockKey);

        boolean tryLock;
        try {
            tryLock = rLock.tryLock(30, 30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            throw new BaseException( "请勿重复提交");
        }
        if (!tryLock) {
            throw new BaseException( "请勿重复提交");
        }
        try {
            UnionAccountDO account = accountMapper.findFirstByUserId(userId);
            if (account == null) {
                account = initAccount(userId);
            }

            //日志，操作前资金
            UnionAccountLogDO log1 = new UnionAccountLogDO();
            log1.setTotalOld(account.getTotal());
            log1.setBalanceOld(account.getBalance());
            log1.setExpendOld(account.getExpend());
            log1.setExpendNew(account.getExpend().add(tradeAmount));
            log1.setTotal(account.getTotal().subtract(tradeAmount));
            log1.setBalanceNew(account.getBalance().subtract(tradeAmount));
            log1.setAccountId(account.getId());
            log1.setLogType("EXPEND");
            log1.setAmount(tradeAmount);
            log1.setExpend(tradeAmount);
            log1.setBalance(tradeAmount);
            log1.setTargetId(targetId);
            log1.setTargetCode("");
            log1.setUserId(userId);
            log1.setTargetName(targetType.getMessage());
            log1.setTargetType(targetType.name());
            log1.setSummary(summary);
            log1.setIncome(BigDecimal.ZERO);
            log1.setIncomeNew(account.getIncome());
            log1.setIncomeOld(account.getIncome());
            log1.setFrost(BigDecimal.ZERO);
            log1.setFrostNew(account.getFrost());
            log1.setFrostOld(account.getFrost());
            int i = accountLogMapper.insert(log1);
            if (i < 1) {
                throw new BaseException( "插入失败");
            }
            account.setTotal(account.getTotal().subtract(tradeAmount));
            account.setExpend(account.getExpend().add(tradeAmount));
            account.setBalance(account.getBalance().subtract(tradeAmount));
            i = accountMapper.updateById(account);
            if (i < 1) {
                throw new BaseException( "更新失败");
            }
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    public UnionAccountDO initAccount(Long userId) {
        UnionAccountDO account = accountMapper.findFirstByUserId(userId);
        if (account != null) {
            return account;
        }

        account = new UnionAccountDO();
        account.setUserId(userId);
        account.setTotal(BigDecimal.ZERO);
        account.setIncome(BigDecimal.ZERO);
        account.setExpend(BigDecimal.ZERO);
        account.setBalance(BigDecimal.ZERO);
        account.setFrost(BigDecimal.ZERO);
        account.setVersion(0);
        int i = accountMapper.insert(account);
        if (i < 1) {
            throw new BaseException( "插入失败");
        }
        return account;
    }
}
