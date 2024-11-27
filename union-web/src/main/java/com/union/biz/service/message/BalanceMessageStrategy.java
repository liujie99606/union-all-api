package com.union.biz.service.message;

import com.union.biz.mapper.AccountMapper;
import com.union.biz.mapper.UnionUserMapper;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.model.UnionUserDO;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class BalanceMessageStrategy implements MessageStrategy {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UnionUserMapper unionUserMapper;


    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.BALANCE;
    }

    @Override
    public Object execute(String text, String userId) {
        Optional<UnionAccountDO> accountOp = Optional.ofNullable(accountMapper.selectOne(UnionAccountDO::getUserId, userId));
        Map<String, Object> map = new HashMap<>();
        //总提现金额
        map.put("total", accountOp.map(UnionAccountDO::getTotal).orElse(BigDecimal.ZERO));
        //可提现金额
        map.put("balance", accountOp.map(UnionAccountDO::getBalance).orElse(BigDecimal.ZERO));
        //提现中金额
        map.put("frost", accountOp.map(UnionAccountDO::getFrost).orElse(BigDecimal.ZERO));
        //昵称
        Optional<UnionUserDO> userOp = Optional.ofNullable(unionUserMapper.selectById(userId));
        map.put("nickname", userOp.map(UnionUserDO::getNickname).orElse(null));
        return FreemarkerUtils.freeMarkerRender(map, "/textMessage/balance.ftl");
    }
}