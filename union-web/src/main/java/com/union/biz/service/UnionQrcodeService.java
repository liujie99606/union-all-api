package com.union.biz.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.union.base.exception.BaseException;
import com.union.biz.mapper.UnionQrcodeMapper;
import com.union.biz.model.UnionQrcodeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author lj
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnionQrcodeService extends ServiceImpl<UnionQrcodeMapper, UnionQrcodeDO> {

    private final UnionQrcodeMapper unionQrcodeMapper;

    /**
     * 保存二维码
     *
     * @param userId        用户ID
     * @param expireSeconds 过期时间(秒)
     * @param actionName    二维码类型
     * @return 二维码记录
     */
    public UnionQrcodeDO saveOrQueryQrcode(Long userId, Long expireSeconds, String actionName) {
        // 查询用户最后一条记录
        UnionQrcodeDO lastQrcode = lambdaQuery()
                .eq(UnionQrcodeDO::getUserId, userId)
                .orderByDesc(UnionQrcodeDO::getCreateTime)
                .last("LIMIT 1")
                .one();

        // 判断是否过期
        if (lastQrcode != null) {
            LocalDateTime expireTime = lastQrcode.getCreateTime()
                    .plusSeconds(lastQrcode.getExpireSeconds());

            if (LocalDateTime.now().isBefore(expireTime) && StrUtil.isNotBlank(lastQrcode.getUrl())) {
                return lastQrcode;
            }
        }
        return saveQrcode(userId, expireSeconds, actionName);
    }

    public UnionQrcodeDO saveQrcode(Long userId, Long expireSeconds, String actionName) {
        // 创建新的二维码记录
        UnionQrcodeDO qrcodeDO = new UnionQrcodeDO();
        qrcodeDO.setUserId(userId);
        qrcodeDO.setExpireSeconds(expireSeconds);
        qrcodeDO.setActionName(actionName);
        qrcodeDO.setCreator(userId + "");
        int i = unionQrcodeMapper.insert(qrcodeDO);
        if (i < 1) {
            throw new BaseException( "保存二维码失败");
        }
        return qrcodeDO;
    }

    /**
     * 更新二维码url
     *
     * @param id  二维码ID
     * @param url 二维码url
     */
    public void updateQrcodeUrl(Long id, String url) {
        UnionQrcodeDO qrcodeDO = new UnionQrcodeDO();
        qrcodeDO.setId(id);
        qrcodeDO.setUrl(url);
        int i = unionQrcodeMapper.updateById(qrcodeDO);
        if (i < 1) {
            throw new BaseException( "更新二维码URL失败");
        }
    }


}