package com.union.event;

import com.union.biz.model.UnionAccountDO;
import com.union.enums.AccountTargetTypeEnum;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class UnionAccountUpdateEvent extends ApplicationEvent {

    /// 构造方法中构建事件内容
    public UnionAccountUpdateEvent(ApplicationContext source,
                                   UnionAccountDO account,
                                   BigDecimal changeAmount,
                                   AccountTargetTypeEnum targetType) {
        super(source);
        this.account = account;
        this.changeAmount = changeAmount;
        this.targetType = targetType;
    }

    private final UnionAccountDO account;
    //变化金额
    private final BigDecimal changeAmount;
    private final AccountTargetTypeEnum targetType;


}
