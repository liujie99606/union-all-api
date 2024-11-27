package com.union.biz.service.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.union.biz.dto.InviteStatisticsRespDto;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class InviteListMessageStrategy implements MessageStrategy {


    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.INVITE_STATISTICS;
    }

    @Override
    public Object execute(String text, String userId) {
        InviteStatisticsRespDto dto = new InviteStatisticsRespDto();

        Map<String, Object> map = JSON.parseObject(JSON.toJSONString(dto), new TypeReference<Map<String, Object>>() {
        });
        return FreemarkerUtils.freeMarkerRender(map, "/textMessage/inviteList.ftl");
    }
}