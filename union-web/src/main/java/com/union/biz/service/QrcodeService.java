package com.union.biz.service;

import com.union.base.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;


@Slf4j
@Service
@Validated
public class QrcodeService {

    @Resource
    private WxMpService wxMpService;

    public String createQrCode(String appId, String sceneStr) {
        String qrCodeUrl;
        try {
            WxMpQrCodeTicket qrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(sceneStr, 2592000);
            qrCodeUrl = wxMpService.getQrcodeService().qrCodePictureUrl(qrCodeTicket.getTicket());
        } catch (WxErrorException e) {
            throw new BaseException("创建二维码失败");
        }
        return qrCodeUrl;
    }

}
