package com.union.event;

import com.union.biz.model.UnionOrderDO;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

@Getter
public class UnionOrderSettleEvent extends ApplicationEvent {

    /// 构造方法中构建事件内容
    public UnionOrderSettleEvent(ApplicationContext source,
                                 UnionOrderDO unionOrderDO) {
        super(source);
        this.unionOrderDO = unionOrderDO;
    }

    private final UnionOrderDO unionOrderDO;


}
