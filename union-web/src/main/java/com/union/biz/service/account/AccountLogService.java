package com.union.biz.service.account;

import com.union.biz.mapper.AccountLogMapper;
import com.union.biz.model.UnionAccountLogDO;
import com.union.biz.vo.AccountLogPageReqVO;
import com.union.biz.vo.AppAccountLogPageReqVO;
import com.union.config.mybatis.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class AccountLogService {

    @Resource
    private AccountLogMapper accountLogMapper;

    public PageResult<UnionAccountLogDO> getPageList(AccountLogPageReqVO pageReqVO) {
        return accountLogMapper.selectPageList(pageReqVO);
    }

    public PageResult<UnionAccountLogDO> getPageList(AppAccountLogPageReqVO pageReqVO) {
        return accountLogMapper.selectPageList(pageReqVO);
    }

    public void create(UnionAccountLogDO log) {
        accountLogMapper.insert(log);
    }


}
