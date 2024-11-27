package com.union.biz.manager;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class MpMessageSendManager {

    @Resource
    private  WxMpService wxMpService;

    public void sendOrderSuccessMessage(String appId, String openId,
                                        String orderNo, String amount, String orderTime) {
        try {
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(openId)
                    .templateId("rl64ZLBvLxDyriK89RyPqgZ_UevNoPZ0-ni-a9aS3t0")
                    // 可以设置点击消息跳转的链接
                    //todo 跳转地址
//                    .url("https://your-domain.com/order-detail?orderNo=" + orderNo)
                    .build();

            // 设置模板数据
            templateMessage.addData(new WxMpTemplateData("character_string1", orderNo)); // 订单编号
            templateMessage.addData(new WxMpTemplateData("time5", orderTime)); // 下单时间
            templateMessage.addData(new WxMpTemplateData("amount4", amount)); // 订单金额

            // 发送模板消息
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            log.info("[sendOrderSuccessMessage][发送订单成功通知 orderNo({}) openId({}) amount({}) orderTime({})]",
                    orderNo, openId, amount, orderTime);
        } catch (Exception e) {
            log.error("[sendOrderSuccessMessage][发送订单成功通知失败：orderNo({}) openId({}) amount({}) orderTime({})]",
                    orderNo, openId, amount, orderTime, e);
        }
    }

    /**
     * 发送商户结算到账通知
     *
     * @param appId         微信公众号 appId
     * @param openId        用户 openId
     * @param incomeAmount  收益金额
     * @param balanceAmount 账户余额
     */
    public void sendMerchantSettlementMessage(String appId, String openId,
                                              String incomeAmount, String balanceAmount) {
        try {
            //todo 跳转地址
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(openId)
                    .templateId("RG75KweDJRsEYbtiBjgAQLnkWWFAxUIqb65MaEU2e14")
                    .build();

            // 设置模板数据
            templateMessage.addData(new WxMpTemplateData("amount6", incomeAmount)); // 收益金额
            templateMessage.addData(new WxMpTemplateData("amount7", balanceAmount)); // 账户余额

            // 发送模板消息
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            log.info("[sendMerchantSettlementMessage][发送商户结算到账通知成功 openId({}) incomeAmount({}) balanceAmount({})]",
                    openId, incomeAmount, balanceAmount);
        } catch (Exception e) {
            log.error("[sendMerchantSettlementMessage][发送商户结算到账通知失败：openId({}) incomeAmount({}) balanceAmount({})]",
                    openId, incomeAmount, balanceAmount, e);
        }
    }
}
