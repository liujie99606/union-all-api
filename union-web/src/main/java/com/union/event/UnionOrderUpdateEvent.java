package com.union.event;

import com.union.biz.model.UnionOrderDO;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

@Getter
public class UnionOrderUpdateEvent extends ApplicationEvent {

    /// 构造方法中构建事件内容
    public UnionOrderUpdateEvent(ApplicationContext source,
                                 UnionOrderDO unionOrderDO,
                                 Integer type) {
        super(source);
        this.unionOrderDO = unionOrderDO;
        this.type = type;
    }

    private final UnionOrderDO unionOrderDO;

    //类型，1新增，2修改
    private final Integer type;


}
