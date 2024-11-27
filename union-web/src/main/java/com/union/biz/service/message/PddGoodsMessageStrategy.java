package com.union.biz.service.message;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.union.base.exception.BaseException;
import com.union.biz.dto.RebateGoodsDto;
import com.union.biz.manager.UnionLinkManager;
import com.union.biz.mapper.UnionUserMapper;
import com.union.biz.model.UnionUserDO;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.FreemarkerUtils;
import com.union.utils.PddUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PddGoodsMessageStrategy extends GoodsMessageStrategy {

    @Resource
    private UnionLinkManager unionLinkManager;

    @Resource
    private UnionUserMapper unionUserMapper;

    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.PDD;
    }

    @Override
    public Object execute(String text, String userId) {
        try {
            RebateGoodsDto rebateGoodsDto = getGoodsInfo(text, userId);
            if (rebateGoodsDto != null) {
                log.info("商品详情:{}", JSON.toJSONString(rebateGoodsDto));
                //转成Map<String, Object>
                Map<String, Object> map = JSON.parseObject(JSON.toJSONString(rebateGoodsDto), new TypeReference<Map<String, Object>>() {
                });
                //购买时间，当前时间一小时后

                map.put("buyTime", DateUtil.format(LocalDateTime.now().plusHours(1), "yyyy-MM-dd HH:mm:ss"));
                return FreemarkerUtils.freeMarkerRender(map, "/textMessage/pdd.ftl");
            }
            return FreemarkerUtils.freeMarkerRender(null, "/textMessage/notDiscount.txt");
        } catch (BaseException e) {
            return e.getMessage();
        } catch (Exception e) {
            log.error("查询失败", e);
            return FreemarkerUtils.freeMarkerRender(null, "/textMessage/notDiscount.txt");
        }
    }

    @Override
    public RebateGoodsDto getGoodsInfo(String text, String userId) {
        UnionUserDO userDO = unionUserMapper.selectById(userId);
        if (userDO == null) {
            throw new BaseException( "用户不存在");
        }
        boolean isBindPdd = Optional.ofNullable(userDO.getIsBindPdd()).orElse(false);
        try {
            RebateGoodsDto rebateGoodsDto = PddUtils.getPddUrl(text, userId, isBindPdd);
            if (!isBindPdd) {
                bindPdd(userId);
            }
            unionLinkManager.saveGoodsInfo(text, JSON.toJSONString(rebateGoodsDto), userId, getType(), rebateGoodsDto.getGoodsId());
            return rebateGoodsDto;
        } catch (BaseException e) {
            Integer code = e.getCode();
            if (!isBindPdd && code != null && code.equals(700)) {
                bindPdd(userId);
            }
            throw e;
        }
    }

    private void bindPdd(String userId) {
        log.info("用户未绑定拼多多,开始绑定");
        UnionUserDO updateUser = new UnionUserDO();
        updateUser.setId(userId);
        updateUser.setIsBindPdd(true);
        unionUserMapper.updateById(updateUser);
    }
}