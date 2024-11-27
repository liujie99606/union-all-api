package com.union.biz.service.message;

import com.union.base.exception.BaseException;
import com.union.biz.mapper.AccountMapper;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.service.account.AccountCashService;
import com.union.biz.vo.AccountCashApplyReqVO;
import com.union.enums.TextMessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
public class WithdrawMessageStrategy implements MessageStrategy {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AccountCashService accountCashService;

    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.WITHDRAW;
    }

    @Override
    public Object execute(String text, String userId) {
        int hour = LocalDateTime.now().getHour();
        if (hour < 9 || hour >= 18) {
            return "提现服务时间为每天9:00-18:00,请在服务时间内操作~";
        }
        UnionAccountDO accountDO = accountMapper.findFirstByUserId(Long.valueOf(userId));
        if ((accountDO == null) || (accountDO.getBalance().compareTo(new BigDecimal("1")) < 0)) {
            return "账户余额大于1元才可以提现哦~";
        }
        AccountCashApplyReqVO req = new AccountCashApplyReqVO();
        req.setUserId(Long.valueOf(userId));
        try {
            accountCashService.apply(req);
            return "申请成功，审核中~";
        } catch (BaseException e) {
            return e.getMessage();
        }
    }

}