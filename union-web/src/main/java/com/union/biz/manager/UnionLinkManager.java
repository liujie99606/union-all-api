package com.union.biz.manager;

import cn.hutool.core.convert.Convert;
import com.union.biz.mapper.UnionLinkMapper;
import com.union.biz.model.UnionLinkDO;
import com.union.enums.TextMessageTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UnionLinkManager {

    @Resource
    private UnionLinkMapper unionLinkMapper;

    public void saveGoodsInfo(String text, String content,
                              String userId, TextMessageTypeEnum textMessageType, String goodsId) {
        UnionLinkDO unionLinkDO = UnionLinkDO.builder()
                .userId(Convert.toLong(userId))
                .orgLink(text)
                .productId(goodsId)
                .content(content)
//                        .shortLink(text)
                .type(textMessageType.name())
                .build();
        unionLinkDO.setCreator(userId);
        unionLinkDO.setDeleted(false);
        unionLinkMapper.insert(unionLinkDO);
    }
}
