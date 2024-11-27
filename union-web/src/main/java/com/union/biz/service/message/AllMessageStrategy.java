package com.union.biz.service.message;

import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AllMessageStrategy implements MessageStrategy {

    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.ALL;
    }

    @Override
    public Object execute(String text, String userId) {
        return FreemarkerUtils.freeMarkerRender(null, "/textMessage/featureList.txt");
    }
}