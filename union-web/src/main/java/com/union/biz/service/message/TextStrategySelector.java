package com.union.biz.service.message;

import cn.hutool.core.util.StrUtil;
import com.union.base.exception.BaseException;
import com.union.biz.dto.CreateUserDto;
import com.union.biz.model.UnionUserDO;
import com.union.biz.service.UnionUserService;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import com.union.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TextStrategySelector {

    private final Map<TextMessageTypeEnum, MessageStrategy> strategyMap = new HashMap<>();

    @Resource
    private UnionUserService unionUserService;

    @Autowired
    public TextStrategySelector(List<MessageStrategy> strategies) {
        for (MessageStrategy strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    public Object process(String request, String openId, Integer userType) {
        request = StrUtil.trim(request);
        TextMessageTypeEnum messageType = TextUtils.getPlatformType(request);
        if (messageType == TextMessageTypeEnum.UNKNOWN) {
            log.info("不支持的消息类型：{}", request);
            return "不支持的消息指令，请按照以下回复。\n" +
                    "\n"
                    + FreemarkerUtils.freeMarkerRender(null, "/textMessage/featureList.txt");
        }

        MessageStrategy strategy = strategyMap.get(messageType);
        if (strategy == null) {
            log.warn("不支持的消息类型，请检查配置：{}", messageType);
            return "不支持的消息指令，请按照以下回复。\n" +
                    "\n"
                    + FreemarkerUtils.freeMarkerRender(null, "/textMessage/featureList.txt");
        }
        UnionUserDO unionUserDO = unionUserService.getOrCreateUser(
                CreateUserDto.builder()
                        .openId(openId)
                        .sourceType(userType)
                        .build()
        );
        if (unionUserDO == null) {
            throw new BaseException( "未查询到用户");
        }

        return strategy.execute(request, unionUserDO.getId());
    }
}
