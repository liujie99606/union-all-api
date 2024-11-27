package com.union.biz.service.account;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.union.base.exception.BaseException;
import com.union.biz.dto.AccountReqDTO;
import com.union.biz.manager.CashNoRedisDAO;
import com.union.biz.mapper.AccountCashMapper;
import com.union.biz.model.UnionAccountCashDO;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.vo.AccountCashApplyReqVO;
import com.union.biz.vo.AccountCashVerifyReqVO;
import com.union.biz.vo.AppAccountCashPageReqVO;
import com.union.config.mybatis.PageResult;
import com.union.constant.RedisKeyConstants;
import com.union.enums.AccountCashStatusEnum;
import com.union.enums.AccountCashVerifyStatusEnum;
import com.union.enums.AccountTargetTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


/**
 * @author lj
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountCashService {

    private final AccountCashMapper accountCashMapper;

    private final AccountService accountService;

    private final CashNoRedisDAO cashNoRedisDAO;


    public void apply(AccountCashApplyReqVO req) {

        UnionAccountDO account = accountService.getAccountByUserId(req.getUserId());
        BigDecimal balance = account.getBalance();
        if (balance.compareTo(new BigDecimal("1")) < 0) {
            throw new BaseException("提现余额不足");
        }
        Long existCash = accountCashMapper.selectCount(Wrappers.lambdaQuery(UnionAccountCashDO.class).eq(UnionAccountCashDO::getCashStatus, AccountCashStatusEnum.PENDING.getStatus()));
        if (existCash > 0) {
            throw new BaseException("存在未审核的提现申请");
        }
        accountCashMapper.insert(UnionAccountCashDO.builder()
                .cashStatus(AccountCashStatusEnum.PENDING.getStatus())
                .userId(req.getUserId())
                .tradeNo(cashNoRedisDAO.generate(RedisKeyConstants.CASH_NO_PREFIX))
                .total(balance)
                .build());
    }

    @Transactional
    public void verify(AccountCashVerifyReqVO req) {
        UnionAccountCashDO cash = accountCashMapper.selectById(req.getId());
        if (!Objects.equals(cash.getCashStatus(), AccountCashStatusEnum.PENDING.getStatus())) {
            throw new BaseException("提现申请已审核");
        }

        cash.setCashStatus(req.getStatusEnum().getStatus());
        cash.setVerifyRemark(req.getRemark());
        cash.setVerifyUserId(req.getUserId());
        cash.setVerifyTime(new Date());
        accountCashMapper.updateById(cash);

        //审核通过执行提现
        if (Objects.equals(req.getStatusEnum().getStatus(), AccountCashVerifyStatusEnum.PASS.getStatus())) {
            accountService.expend(AccountReqDTO.builder()
                    .userId(cash.getUserId())
                    .summary("提现审核通过")
                    .tradeAmount(cash.getTotal())
                    .targetType(AccountTargetTypeEnum.CASH)
                    .targetId(cash.getId())
                    .build());
        }
    }

    public PageResult<UnionAccountCashDO> getPageList(AppAccountCashPageReqVO req) {
        return accountCashMapper.selectPageList(req);
    }

}
