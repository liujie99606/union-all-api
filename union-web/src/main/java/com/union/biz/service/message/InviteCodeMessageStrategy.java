package com.union.biz.service.message;

import com.union.biz.model.UnionQrcodeDO;
import com.union.biz.service.QrcodeService;
import com.union.biz.service.UnionQrcodeService;
import com.union.enums.TextMessageTypeEnum;
import com.union.utils.UserRateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class InviteCodeMessageStrategy implements MessageStrategy {

    @Resource
    private UnionQrcodeService unionQrcodeService;

    @Resource
    private QrcodeService qrcodeService;

    @Value("${wechat.mp.app-id}")
    private String appId;

    @Override
    public TextMessageTypeEnum getType() {
        return TextMessageTypeEnum.INVITE_CODE;
    }

    @Override
    public Object execute(String text, String userId) {
        UnionQrcodeDO qrcodeDO = unionQrcodeService.saveQrcode(Long.valueOf(userId), 2592000L, "QR_STR_SCENE");
        String qrCode = qrcodeService.createQrCode(appId, String.valueOf(qrcodeDO.getId()));
        unionQrcodeService.updateQrcodeUrl(qrcodeDO.getId(), qrCode);
        log.info("邀请码：{}", qrCode);
        return UserRateUtils.generateReferralRateDescription() + "\n" +
                "⬇\n"
                + qrCode;
    }

}