package com.union.biz.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkOrderListIncrementGetResponse;
import com.taobao.top.ability414.domain.TaobaoTbkOrderDetailsGetPublisherOrderDto;
import com.union.base.exception.BaseException;
import com.union.biz.dto.SyncOrderDto;
import com.union.biz.manager.AccountManager;
import com.union.biz.manager.MpMessageSendManager;
import com.union.biz.mapper.UnionLinkMapper;
import com.union.biz.mapper.UnionOrderLogMapper;
import com.union.biz.mapper.UnionOrderMapper;
import com.union.biz.mapper.UnionUserMapper;
import com.union.biz.model.*;
import com.union.biz.service.platform.JdService;
import com.union.biz.service.platform.TaobaoUtils;
import com.union.biz.vo.AppOrderPageReqVO;
import com.union.config.mybatis.PageResult;
import com.union.enums.AccountTargetTypeEnum;
import com.union.enums.SettleStatusEnum;
import com.union.enums.TextMessageTypeEnum;
import com.union.event.OrderSyncEvent;
import com.union.event.UnionAccountUpdateEvent;
import com.union.event.UnionOrderSettleEvent;
import com.union.event.UnionOrderUpdateEvent;
import com.union.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * @author lj
 */
@Service
@Slf4j
public class UnionOrderService extends ServiceImpl<UnionOrderMapper, UnionOrderDO> {

    @Resource
    private UnionOrderMapper unionOrderMapper;

    @Resource
    private UnionOrderLogMapper unionOrderLogMapper;

    @Resource
    private JdService jdService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private AccountManager accountManager;

    @Resource
    private UnionUserMapper unionUserMapper;

    @Resource
    private UnionLinkMapper unionLinkMapper;

    @Resource
    private MpMessageSendManager mpMessageSendManager;

    @Value("${wechat.mp.app-id}")
    private String appId;


    public void jdOrderSync(String startTime, String endTime) {
        List<OrderRowResp> orderRespList = jdService.getOrderRowInfo(startTime, endTime);
        if (CollUtil.isEmpty(orderRespList)) {
            log.info("没有拉取到京东订单数据");
            return;
        }
        for (OrderRowResp orderRowResp : orderRespList) {
            SyncOrderDto syncOrderDto = jdOrderToSyncOrderDto(orderRowResp);
            applicationContext.publishEvent(new OrderSyncEvent(applicationContext, syncOrderDto));
        }
    }

    public void taobaoOrderSync(String startTime, String endTime) {
        List<TaobaoTbkOrderDetailsGetPublisherOrderDto> orderList = TaobaoUtils.taobaoTbkOrderDetailsGet(startTime, endTime);
        if (CollUtil.isEmpty(orderList)) {
            log.info("没有拉取到淘宝订单数据");
            return;
        }
        for (TaobaoTbkOrderDetailsGetPublisherOrderDto orderRowResp : orderList) {
            SyncOrderDto syncOrder = new SyncOrderDto();
            syncOrder.setPlatform(TextMessageTypeEnum.TAO_BAO.name());
            syncOrder.setProductName(TextUtils.handleGoodsName(orderRowResp.getItemTitle()));
            syncOrder.setOrderNo(orderRowResp.getTradeId());
            syncOrder.setData(JSON.toJSONString(orderRowResp));
            syncOrder.setProductId(TextUtils.getTaobaoGoodsId(orderRowResp.getItemId()));
            UnionLinkDO linkDO = unionLinkMapper.selectOne(new LambdaQueryWrapper<UnionLinkDO>()
                    .eq(UnionLinkDO::getProductId, syncOrder.getProductId())
                    .eq(UnionLinkDO::getType, TextMessageTypeEnum.TAO_BAO.name())
                    .ge(UnionLinkDO::getCreateTime, LocalDateTime.now().minusDays(3))
                    .orderByDesc(UnionLinkDO::getId)
                    .last("limit 1")
            );
            log.info("查询到链接为：{}", linkDO);
            if (linkDO != null) {
                syncOrder.setUserId(linkDO.getUserId());
            }
            //实际金额
            syncOrder.setPayPrice(Convert.toDouble(orderRowResp.getPayPrice()));
            syncOrder.setRebatePrice(Convert.toDouble(orderRowResp.getPubShareFee()));
            //预估金额
            syncOrder.setEstimatePaymentAmount(Convert.toDouble(orderRowResp.getAlipayTotalPrice()));
            syncOrder.setEstimateRebateAmount(Convert.toDouble(orderRowResp.getPubSharePreFee()));
            //格式化状态
            syncOrder.setStatus(formatTaobaoOrderStatus(orderRowResp.getTkStatus()));
            applicationContext.publishEvent(new OrderSyncEvent(applicationContext, syncOrder));
        }
    }


    /**
     * 同步拼多多订单
     */
    public void pddOrderSync(LocalDateTime startTime, LocalDateTime endTime) {
        List<PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem> orderList = PddUtils.getOrderListIncrement(
                startTime.atZone(ZoneId.systemDefault()).toEpochSecond()
                , endTime.atZone(ZoneId.systemDefault()).toEpochSecond());
        if (CollUtil.isEmpty(orderList)) {
            log.info("没有拉取到拼多多订单数据");
            return;
        }
        for (PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem orderRowResp : orderList) {
            SyncOrderDto syncOrder = pddOrderToSyncOrderDto(orderRowResp);
            applicationContext.publishEvent(new OrderSyncEvent(applicationContext, syncOrder));
        }
    }

    private SyncOrderDto pddOrderToSyncOrderDto(PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem orderRowResp) {
        SyncOrderDto syncOrder = new SyncOrderDto();
        syncOrder.setPlatform(TextMessageTypeEnum.PDD.name());
        syncOrder.setProductName(TextUtils.handleGoodsName(orderRowResp.getGoodsName()));
        syncOrder.setOrderNo(orderRowResp.getOrderSn());
        syncOrder.setData(JSON.toJSONString(orderRowResp));
        syncOrder.setProductId(orderRowResp.getGoodsSign());
        syncOrder.setUserId(
                Optional.ofNullable(orderRowResp.getCustomParameters())
                        .filter(StrUtil::isNotBlank)
                        .map(JSON::parseObject)
                        .map(jsonObject -> jsonObject.getString("uid"))
                        .map(Long::valueOf)
                        .orElse(null)
        );
        //实际金额
        syncOrder.setPayPrice(CurrencyConverter.convertCentsToYuan(orderRowResp.getOrderAmount()));
        syncOrder.setRebatePrice(CurrencyConverter.convertCentsToYuan(orderRowResp.getPromotionAmount()));
        //预估金额
        syncOrder.setEstimatePaymentAmount(CurrencyConverter.convertCentsToYuan(orderRowResp.getOrderAmount()));
        syncOrder.setEstimateRebateAmount(CurrencyConverter.convertCentsToYuan(orderRowResp.getPromotionAmount()));
        //格式化状态
        syncOrder.setStatus(formatPddOrderStatus(orderRowResp.getOrderStatus()));
        return syncOrder;
    }


    @Transactional(rollbackFor = Exception.class)
    @EventListener
    public void onJdOrderEventToSaveOrder(OrderSyncEvent event) {
        SyncOrderDto syncOrderDto = event.getSyncOrderDto();

        String orderNo = syncOrderDto.getOrderNo();
        Long userId = syncOrderDto.getUserId();
        String platform = syncOrderDto.getPlatform();

        UnionOrderDO oldUnionOrderDO = unionOrderMapper.findFirstByOrderCodeAndPlatformId(orderNo, platform);
        if (oldUnionOrderDO == null) {
            UnionOrderDO unionOrderDO = new UnionOrderDO();
            unionOrderDO.setOrderCode(orderNo);
            unionOrderDO.setPlatformId(platform);
            unionOrderDO.setProductId(syncOrderDto.getProductId());
            unionOrderDO.setUserId(userId);
            unionOrderDO.setCreator(userId + "");
            unionOrderDO.setInviterUserId(
                    Optional.ofNullable(userId)
                            .map(unionUserMapper::selectById)
                            .map(UnionUserDO::getInviterUserId)
                            .orElse(null)
            );
            unionOrderDO.setProductName(syncOrderDto.getProductName());
            unionOrderDO.setSettleStatus(SettleStatusEnum.WAIT_SETTLE.getCode());
            //以上字段不会变更
            changeOldValues(unionOrderDO, syncOrderDto);
            unionOrderMapper.insert(unionOrderDO);

            UnionOrderLogDO orderLog = new UnionOrderLogDO();
            orderLog.setOrderId(unionOrderDO.getId());
            orderLog.setLogContent(syncOrderDto.getData());
            orderLog.setCreator(userId + "");
            orderLog.setOrderStatus(syncOrderDto.getStatus());
            unionOrderLogMapper.insert(orderLog);

            applicationContext.publishEvent(new UnionOrderUpdateEvent(applicationContext, unionOrderDO, 1));
        } else {
            //判断是否需要更新
            if (StrUtil.equals(oldUnionOrderDO.getData(), syncOrderDto.getData())) {
                log.info("订单数据未发生改变");
                return;
            }

            //取值之前的佣金值
            BigDecimal oldRebateAmount = oldUnionOrderDO.getUnionRebateAmount();
            changeOldValues(oldUnionOrderDO, syncOrderDto);
            unionOrderMapper.updateById(oldUnionOrderDO);

            UnionOrderLogDO orderLog = new UnionOrderLogDO();
            orderLog.setOrderId(oldUnionOrderDO.getId());
            orderLog.setLogContent(syncOrderDto.getData());
            orderLog.setCreator(userId + "");
            orderLog.setOrderStatus(syncOrderDto.getStatus());
            unionOrderLogMapper.insert(orderLog);

            // 处理部分退款情况
            if (SettleStatusEnum.SETTLED.getCode().equals(oldUnionOrderDO.getSettleStatus())
                    && oldUnionOrderDO.getUserId() != null
                    && oldUnionOrderDO.getUserRebateAmount() != null) {
                BigDecimal newRebateAmount = Convert.toBigDecimal(syncOrderDto.getRebatePrice());
                if (newRebateAmount != null && newRebateAmount.compareTo(oldRebateAmount) < 0) {
                    //新的用户拥金
                    BigDecimal newUserRebateAmount = UserRateUtils.calculateRebateAmount(newRebateAmount);
                    BigDecimal diffAmount = oldUnionOrderDO.getUserRebateAmount().subtract(newUserRebateAmount);

                    log.info("[onJdOrderEventToSaveOrder][订单({})发生部分退款, 原佣金:{}, 新佣金:{}, 差额:{}]",
                            orderNo, oldUnionOrderDO.getUserRebateAmount(), newUserRebateAmount, diffAmount);

                    //差额大于5毛才去考虑部分退款逻辑
                    if (diffAmount.compareTo(new BigDecimal("0.5")) > 0) {
                        oldUnionOrderDO.setUserRebateAmount(newUserRebateAmount);
                        accountManager.expend(oldUnionOrderDO.getUserId(), diffAmount,
                                oldUnionOrderDO.getId(), AccountTargetTypeEnum.REFUND, "部分退款扣除佣金");
                    }
                    //平台佣金重新计算
                    oldUnionOrderDO.setPlatformRebateAmount(oldUnionOrderDO.getUnionRebateAmount()
                            .subtract(oldUnionOrderDO.getUserRebateAmount())
                            .subtract(oldUnionOrderDO.getPartnerRebateAmount())
                    );
                    int i = unionOrderMapper.updateById(oldUnionOrderDO);
                    if (i < 1) {
                        throw new BaseException( "结算失败");

                    }
                }
            }
        }
    }

    private void changeOldValues(UnionOrderDO unionOrderDO, SyncOrderDto syncOrderDto) {
        //实际金额
        unionOrderDO.setPaymentAmount(Convert.toBigDecimal(syncOrderDto.getPayPrice()));
        unionOrderDO.setUnionRebateAmount(Convert.toBigDecimal(syncOrderDto.getRebatePrice()));
        //预估金额
        unionOrderDO.setEstimatePaymentAmount(Convert.toBigDecimal(syncOrderDto.getEstimatePaymentAmount()));
        unionOrderDO.setEstimateRebateAmount(Convert.toBigDecimal(syncOrderDto.getEstimateRebateAmount()));
        unionOrderDO.setEstimateUserRebateAmount(UserRateUtils.calculateRebateAmount(unionOrderDO.getEstimateRebateAmount()));

        unionOrderDO.setData(syncOrderDto.getData());
        unionOrderDO.setOrderStatus(syncOrderDto.getStatus());
    }

    private SyncOrderDto jdOrderToSyncOrderDto(OrderRowResp orderRowResp) {
        SyncOrderDto syncOrder = new SyncOrderDto();
        syncOrder.setPlatform(TextMessageTypeEnum.JD.name());
        syncOrder.setProductName(TextUtils.handleGoodsName(orderRowResp.getSkuName()));
        syncOrder.setOrderNo(String.valueOf(orderRowResp.getOrderId()));
        syncOrder.setData(JSON.toJSONString(orderRowResp));
        syncOrder.setProductId(String.valueOf(orderRowResp.getSkuId()));
        if (StrUtil.isNotBlank(orderRowResp.getSubUnionId())) {
            syncOrder.setUserId(Long.valueOf(orderRowResp.getSubUnionId()));
        }
        //实际金额
        syncOrder.setPayPrice(orderRowResp.getActualCosPrice());
        syncOrder.setRebatePrice(orderRowResp.getActualFee());
        //预估金额
        syncOrder.setEstimatePaymentAmount(orderRowResp.getEstimateCosPrice());
        syncOrder.setEstimateRebateAmount(orderRowResp.getEstimateFee());
        Integer validCode = orderRowResp.getValidCode();
        syncOrder.setStatus(formatJdOrderStatus(validCode));
        return syncOrder;
    }

    private Integer formatJdOrderStatus(Integer validCode) {
        //订单状态,1：未完成；2：已完成；5：无效订单
        if (validCode <= 14) {
            return 5;
        }
        if (validCode <= 16) {
            return 1;
        }
        if (validCode == 17) {
            return 2;
        }
        return 5;
    }

    /**
     * 淘客订单状态，11-拍下未付款，12-付款，13-关闭，14-确认收货，3-结算成功;不传，表示所有状态
     */
    private Integer formatTaobaoOrderStatus(Long tkStatus) {
        //订单状态,1：未完成；2：已完成；5：无效订单
        if (tkStatus == 11) {
            return 1;
        }
        if (tkStatus == 12) {
            return 1;
        }
        if (tkStatus == 13) {
            return 5;
        }
        if (tkStatus == 14) {
            return 1;
        }
        if (tkStatus == 3) {
            return 2;
        }
        return 5;
    }


    /**
     * 订单状态：0-已支付；1-已成团；2-确认收货；3-审核成功；4-审核失败（不可提现）；5-已经结算 ;10-已处罚
     */
    private Integer formatPddOrderStatus(Integer orderStatus) {
        //订单状态,1：未完成；2：已完成；5：无效订单
        if (orderStatus == 0
                || orderStatus == 1
                || orderStatus == 2
                || orderStatus == 3) {
            return 1;
        }
        if (orderStatus == 5) {
            return 2;
        }
        return 5;
    }

    /**
     * 执行订单结算
     */
    public void unionOrderSettle() {
        List<UnionOrderDO> unionOrderDOList = unionOrderMapper.selectAllWaitSettleOrder();
        if (CollUtil.isEmpty(unionOrderDOList)) {
            log.info("没有待结算的订单");
            return;
        }
        for (UnionOrderDO unionOrderDO : unionOrderDOList) {
            applicationContext.publishEvent(new UnionOrderSettleEvent(applicationContext, unionOrderDO));
        }
    }

    /**
     * 结算
     */
    @Transactional(rollbackFor = Exception.class)
    @EventListener
    public void onUnionOrderSettleEventToSettle(UnionOrderSettleEvent event) {
        UnionOrderDO unionOrderDO = event.getUnionOrderDO();
        Long userId = unionOrderDO.getUserId();
        Long inviterUserId = unionOrderDO.getInviterUserId();
        UnionUserDO unionUserDO = Optional.ofNullable(userId)
                .map(unionUserMapper::selectById)
                .orElse(null);
        if (unionUserDO == null) {
            log.info("用户未绑定用户信息，无法结算订单");
            //全部结算给平台
            unionOrderDO.setUserRebateAmount(BigDecimal.ZERO);
            unionOrderDO.setPartnerRebateAmount(BigDecimal.ZERO);
            unionOrderDO.setPlatformRebateAmount(unionOrderDO.getUnionRebateAmount());
            unionOrderDO.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
            int i = unionOrderMapper.updateById(unionOrderDO);
            if (i < 1) {
                throw new BaseException( "结算失败");
            }
            return;
        }
        //联盟返利金额
        BigDecimal unionRebateAmount = unionOrderDO.getUnionRebateAmount();
        //小于等于0，先不结算
        if (unionRebateAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("订单佣金金额小于等于0，不结算,{}", unionOrderDO);
            return;
        }
        //用户所得拥金
        BigDecimal userRebateAmount = UserRateUtils.calculateRebateAmount(unionRebateAmount);
        unionOrderDO.setUserRebateAmount(userRebateAmount);
        // 推荐人得拥金，可能每个推荐人不一样
        BigDecimal partnerRebateAmount = BigDecimal.ZERO;
        if (inviterUserId != null) {
            UnionUserDO inviterUser = unionUserMapper.selectById(inviterUserId);
            if (inviterUser != null) {
                // 人数动态查询
                Long inviteUsers = unionUserMapper.countInviteUsers(inviterUserId);
                partnerRebateAmount = UserRateUtils.calculateCommissionByReferralCount(unionRebateAmount,
                        Optional.ofNullable(inviteUsers)
                                .map(Long::intValue)
                                .orElse(0)
                );
            }
        }
        unionOrderDO.setPartnerRebateAmount(partnerRebateAmount);
        unionOrderDO.setPlatformRebateAmount(unionOrderDO.getUnionRebateAmount()
                .subtract(unionOrderDO.getUserRebateAmount())
                .subtract(unionOrderDO.getPartnerRebateAmount())
        );
        unionOrderDO.setSettleStatus(SettleStatusEnum.SETTLED.getCode());
        int i = unionOrderMapper.updateById(unionOrderDO);
        if (i < 1) {
            throw new BaseException( "结算失败");
        }
        if (userRebateAmount.compareTo(BigDecimal.ZERO) > 0) {
            accountManager.income(userId, userRebateAmount,
                    unionOrderDO.getId(), AccountTargetTypeEnum.REBATE_AWARD, "返利奖励");
        }
        if (partnerRebateAmount.compareTo(BigDecimal.ZERO) > 0) {
            accountManager.income(unionOrderDO.getInviterUserId(), partnerRebateAmount,
                    unionOrderDO.getId(), AccountTargetTypeEnum.REBATE_AWARD, "返利奖励");
        }
    }

    public void orderQuery(String text, String userId) {
        String orderCode = OrderUtils.extractOrder(text);
        if (StrUtil.isBlank(orderCode)) {
            log.info("订单号不存在,{}", text);
            throw new BaseException( "请回复有效订单");
        }
        UnionOrderDO unionOrderDO = unionOrderMapper.selectOne(new LambdaQueryWrapper<UnionOrderDO>()
                .eq(UnionOrderDO::getOrderCode, orderCode)
                .eq(UnionOrderDO::getDeleted, false)
                .last("limit 1")
        );
        TextMessageTypeEnum platformType = OrderUtils.getPlatform(orderCode);
        if (platformType == null) {
            log.info("订单不存在2,{}", text);
            throw new BaseException( "请回复有效订单");
        }

        if (unionOrderDO == null) {
            log.info("订单不存在1,{}", text);
            String message = FreemarkerUtils.freeMarkerRender(null, "/textMessage/orderNotExits.txt");
            throw new BaseException( message);
        }

        // 如果订单已经绑定用户,不允许重复绑定
        if (unionOrderDO.getUserId() != null) {
            throw new BaseException( "订单已被绑定,无法重复绑定");
        }
    }

    public PageResult<UnionOrderDO> getPageList(AppOrderPageReqVO pageReqVO) {

        return unionOrderMapper.selectPageList(pageReqVO);
    }

    /**
     * 监听订单更新事件,发送订单成功消息
     *
     * @param event 订单更新事件
     */
    @Async
    @EventListener
    public void handleOrderUpdateEventToSendMessage(UnionOrderUpdateEvent event) {
        UnionOrderDO unionOrderDO = event.getUnionOrderDO();
        Integer type = event.getType();
        if (type == 1) {
            Optional.ofNullable(unionOrderDO)
                    .map(UnionOrderDO::getUserId)
                    .map(userId -> unionUserMapper.selectById(userId))
                    .filter(user -> StrUtil.isNotBlank(user.getOpenId()))
                    .ifPresent(unionUserDO ->
                            mpMessageSendManager.sendOrderSuccessMessage(
                                    appId,
                                    unionUserDO.getOpenId(),
                                    unionOrderDO.getOrderCode(),
                                    Convert.toStr(unionOrderDO.getEstimatePaymentAmount()),
                                    DateUtil.formatLocalDateTime(unionOrderDO.getCreateTime())
                            )
                    );
        }
    }

    /**
     * 监听账号余额更新事件,商户结算到账通知
     */
    @Async
    @EventListener
    public void handleAccountUpdateEventToSendMessage(UnionAccountUpdateEvent event) {
        UnionAccountDO account = event.getAccount();
        BigDecimal changeAmount = event.getChangeAmount();
        AccountTargetTypeEnum targetType = event.getTargetType();
        if (AccountTargetTypeEnum.REBATE_AWARD.equals(targetType)) {
            Optional.ofNullable(account)
                    .map(UnionAccountDO::getUserId)
                    .map(userId -> unionUserMapper.selectById(userId))
                    .filter(user -> StrUtil.isNotBlank(user.getOpenId()))
                    .ifPresent(unionUserDO ->
                            mpMessageSendManager.sendMerchantSettlementMessage(appId,
                                    unionUserDO.getOpenId(),
                                    Convert.toStr(changeAmount),
                                    Convert.toStr(account.getBalance())
                            )
                    );
        }
    }
}
