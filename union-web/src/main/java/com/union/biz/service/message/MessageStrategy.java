package com.union.biz.service.message;


import com.union.enums.TextMessageTypeEnum;

public interface MessageStrategy {
    TextMessageTypeEnum getType();

    Object execute(String request, String userId);
}