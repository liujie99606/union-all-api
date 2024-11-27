package com.union.biz.service.message;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.union.biz.dto.RebateGoodsDto;
import com.union.biz.manager.UnionLinkManager;
import com.union.biz.service.platform.JdService;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import com.union.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class JdGoodsMessageStrategy extends GoodsMessageStrategy {


    @Resource
    private JdService jdService;
    @Resource
    private UnionLinkManager unionLinkManager;


    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.JD;
    }

    @Override
    public Object execute(String text, String userId) {
        RebateGoodsDto rebateGoodsDto = getGoodsInfo(text, userId);
        if (rebateGoodsDto != null) {
            log.info("商品详情:{}", JSON.toJSONString(rebateGoodsDto));
            //转成Map<String, Object>
            Map<String, Object> map = JSON.parseObject(JSON.toJSONString(rebateGoodsDto), new TypeReference<Map<String, Object>>() {
            });
            return FreemarkerUtils.freeMarkerRender(map, "/textMessage/jd.ftl");
        }
        return FreemarkerUtils.freeMarkerRender(null, "/textMessage/notDiscount.txt");
    }

    @Override
    public RebateGoodsDto getGoodsInfo(String text, String userId) {
        //获取商品id
        String goodsId = TextUtils.getJdProductId(text);
        log.info("京东商品id:{}", goodsId);
        if (StrUtil.isNotBlank(goodsId)) {
            String url = TextUtils.extractUrl(text);
            RebateGoodsDto rebateGoodsDto = jdService.urlToDetail(url, goodsId, userId);
            if (rebateGoodsDto != null) {
                unionLinkManager.saveGoodsInfo(text, JSON.toJSONString(rebateGoodsDto), userId, getType(),goodsId);
            }
            return rebateGoodsDto;
        }
        return null;
    }
}