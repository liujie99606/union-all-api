package com.union.biz.service.account;


import com.union.biz.convert.AccountLogConvert;
import com.union.biz.dto.AccountReqDTO;
import com.union.biz.mapper.AccountMapper;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.model.UnionAccountLogDO;
import com.union.biz.vo.AccountPageReqVO;
import com.union.config.mybatis.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class AccountService {

    @Resource
    private AccountMapper accountMapper;

    private AccountLogService accountLogService;

    public PageResult<UnionAccountDO> getPageList(AccountPageReqVO pageReqVO) {

        return accountMapper.selectPageList(pageReqVO);
    }

    public UnionAccountDO getAccountByUserId(Long userId) {

        return accountMapper.findFirstByUserId(userId);
    }


    @Transactional
    public void income(AccountReqDTO req) {
        UnionAccountDO account = accountMapper.findFirstByUserId(req.getUserId());

        UnionAccountLogDO log = AccountLogConvert.INSTANCE.convertToIncomeDo(req, account);
        //日志，操作前资金
        accountLogService.create(log);

        account.setTotal(account.getTotal().add(req.getTradeAmount()));
        account.setIncome(account.getIncome().add(req.getTradeAmount()));
        account.setBalance(account.getBalance().add(req.getTradeAmount()));
        accountMapper.insert(account);
    }

    /**
     * 用户资金扣除
     */
    @Transactional
    public void expend(AccountReqDTO req) {
        UnionAccountDO account = accountMapper.findFirstByUserId(req.getUserId());
        //日志，操作前资金
        UnionAccountLogDO log = AccountLogConvert.INSTANCE.convertToExpendDo(req, account);
        accountLogService.create(log);

        account.setTotal(account.getTotal().subtract(req.getTradeAmount()));
        account.setExpend(account.getExpend().add(req.getTradeAmount()));
        account.setBalance(account.getBalance().subtract(req.getTradeAmount()));
        accountMapper.insert(account);
    }


}
