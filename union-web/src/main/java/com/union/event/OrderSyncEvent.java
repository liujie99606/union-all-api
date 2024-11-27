package com.union.event;

import com.union.biz.dto.SyncOrderDto;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderSyncEvent extends ApplicationEvent {

    /// 构造方法中构建事件内容
    public OrderSyncEvent(ApplicationContext source,
                          SyncOrderDto syncOrderDto) {
        super(source);
        this.syncOrderDto = syncOrderDto;
    }

    private final SyncOrderDto syncOrderDto;


}
