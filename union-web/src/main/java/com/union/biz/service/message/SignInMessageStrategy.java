package com.union.biz.service.message;

import com.union.base.exception.BaseException;
import com.union.biz.model.UnionUserDailySignDO;
import com.union.biz.service.UnionUserDailySignService;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SignInMessageStrategy implements MessageStrategy {

    @Resource
    private UnionUserDailySignService unionUserDailySignService;


    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.SIGN_IN;
    }

    @Override
    public Object execute(String text, String userId) {
        try {
            UnionUserDailySignDO userDailySignDO = unionUserDailySignService.signIn(Long.valueOf(userId));
            Map<String, Object> map = new HashMap<>();
            map.put("amount", userDailySignDO.getAmount());
            map.put("continuousDays", userDailySignDO.getContinuousDays());
            return FreemarkerUtils.freeMarkerRender(map, "/textMessage/signIn.ftl");
        } catch (BaseException e) {
            return e.getMessage();
        }
    }
}