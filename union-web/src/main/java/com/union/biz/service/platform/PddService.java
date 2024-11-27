package com.union.biz.service.platform;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.*;
import com.pdd.pop.sdk.http.api.pop.response.*;
import com.union.base.exception.BaseException;
import com.union.biz.dto.RebateGoodsDto;
import com.union.config.properties.PddProperties;
import com.union.utils.CurrencyConverter;
import com.union.utils.TextUtils;
import com.union.utils.UserRateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PddService {

    private final PopClient client;
    private final PddProperties pddProperties;

    public PddService(PddProperties pddProperties) {
        this.pddProperties = pddProperties;
        this.client = new PopHttpClient(pddProperties.getClientId(), pddProperties.getClientSecret());
    }

    /**
     * 转链
     */
    public PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse getLink(String url, String userId) {
        PddDdkGoodsZsUnitUrlGenRequest request = new PddDdkGoodsZsUnitUrlGenRequest();
        request.setCustomParameters(String.format(pddProperties.getCustomParameters(), userId));
        request.setGenerateShortLink(false);
        request.setPid(pddProperties.getPid());
        request.setSourceUrl(url);
        request.setGenerateWeAppLongLink(false);
        PddDdkGoodsZsUnitUrlGenResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            log.error("调用接口失败", e);
        }
        return Optional.ofNullable(response)
                .map(PddDdkGoodsZsUnitUrlGenResponse::getGoodsZsUnitGenerateResponse)
                .orElse(null);
    }

    /**
     * 查询是否绑定备案
     */
    public PddDdkMemberAuthorityQueryResponse.AuthorityQueryResponse getBindStatus(String userId) {
        PddDdkMemberAuthorityQueryRequest request = new PddDdkMemberAuthorityQueryRequest();
        request.setCustomParameters(String.format(pddProperties.getCustomParameters(), userId));
        request.setPid(pddProperties.getPid());
        PddDdkMemberAuthorityQueryResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            log.error("调用接口失败", e);
        }
        return Optional.ofNullable(response)
                .map(PddDdkMemberAuthorityQueryResponse::getAuthorityQueryResponse)
                .orElse(null);
    }


    /**
     * 多多进宝推广链接生成
     */
    public PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponse getPddLink(String userId) {

        PddDdkRpPromUrlGenerateRequest request = new PddDdkRpPromUrlGenerateRequest();
        request.setCustomParameters(String.format(pddProperties.getCustomParameters(), userId));
        request.setChannelType(10);

        List<String> pIdList = new ArrayList<>();
        pIdList.add(pddProperties.getPid());
        request.setPIdList(pIdList);

        PddDdkRpPromUrlGenerateResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            log.error("调用接口失败", e);
        }
        return Optional.ofNullable(response)
                .map(PddDdkRpPromUrlGenerateResponse::getRpPromotionUrlGenerateResponse)
                .orElse(null);
    }

    /**
     * 多多进宝商品详情查询
     */
    public PddDdkGoodsDetailResponse.GoodsDetailResponseGoodsDetailsItem getGoodsDetail(String userId, String goodsSign) {
        PddDdkGoodsDetailRequest request = new PddDdkGoodsDetailRequest();
        request.setCustomParameters(String.format(pddProperties.getCustomParameters(), userId));
        request.setPid(pddProperties.getPid());
        request.setGoodsSign(goodsSign);
        PddDdkGoodsDetailResponse response;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            log.error("调用接口失败", e);
            return null;
        }
        return Optional.ofNullable(response)
                .map(PddDdkGoodsDetailResponse::getGoodsDetailResponse)
                .map(PddDdkGoodsDetailResponse.GoodsDetailResponse::getGoodsDetails)
                .filter(CollUtil::isNotEmpty)
                .map(s -> s.get(0))
                .orElse(null);
    }

    /**
     * 多多进宝推广链接生成
     */
    public PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem generateUrl(
            String userId, String goodsSign) {
        PddDdkGoodsPromotionUrlGenerateRequest request = new PddDdkGoodsPromotionUrlGenerateRequest();
        request.setCustomParameters(String.format(pddProperties.getCustomParameters(), userId));
//        request.setGenerateShortUrl(true);

        List<String> goodsSignList = new ArrayList<>();
        goodsSignList.add(goodsSign);
        request.setGoodsSignList(goodsSignList);
        request.setPId(pddProperties.getPid());
        PddDdkGoodsPromotionUrlGenerateResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            log.error("调用接口失败", e);
        }
        return Optional.ofNullable(response)
                .map(PddDdkGoodsPromotionUrlGenerateResponse::getGoodsPromotionUrlGenerateResponse)
                .map(PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponse::getGoodsPromotionUrlList)
                .filter(CollUtil::isNotEmpty)
                .map(s -> s.get(0))
                .orElse(null);
    }

    /**
     * 生成拼多多链接
     */
    public RebateGoodsDto getPddUrl(String url, String userId, boolean isBindPdd) {
        String noDiscountMsg = "该商品暂未设置优惠\n请换个商品试试";

        if (!isBindPdd) {
            // 1. 校验用户是否已绑定
            PddDdkMemberAuthorityQueryResponse.AuthorityQueryResponse bindResponse = getBindStatus(userId);

            if (bindResponse == null) {
                throw new BaseException(noDiscountMsg);
            }

            // 2. 如果未绑定,返回绑定链接
            if (bindResponse.getBind() == 0) {
                PddDdkRpPromUrlGenerateResponse.RpPromotionUrlGenerateResponse generateResponse = getPddLink(userId);
                if (generateResponse == null || CollUtil.isEmpty(generateResponse.getUrlList())) {
                    throw new BaseException(noDiscountMsg);
                }
                String mobileUrl = generateResponse.getUrlList().get(0).getMobileUrl();
                throw new BaseException(String.format(pddProperties.getReturnUrlTemplate(), mobileUrl));
            }
        }

        // 3. 获取商品推广链接
        PddDdkGoodsZsUnitUrlGenResponse.GoodsZsUnitGenerateResponse response = getLink(url, userId);
        log.info("获取推广链接结果: {}", JSON.toJSONString(response));
        if (response == null) {
            throw new BaseException(noDiscountMsg);
        }

        // 4. 获取商品编号
        String goodsSign = TextUtils.getGoodsSign(response.getMobileUrl());
        log.info("商品编号: {}", goodsSign);
        if (StrUtil.isBlank(goodsSign)) {
            throw new BaseException(noDiscountMsg);
        }

        // 5. 获取商品详情
        PddDdkGoodsDetailResponse.GoodsDetailResponseGoodsDetailsItem goodsDetail = getGoodsDetail(userId, goodsSign);
        if (goodsDetail == null) {
            throw new BaseException(noDiscountMsg);
        }
        log.info("商品详情: {}", JSON.toJSONString(goodsDetail));

        // 6. 生成推广链接
        PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem generateResponse = generateUrl(userId, goodsSign);
        if (generateResponse == null) {
            throw new BaseException(noDiscountMsg);
        }
        log.info("生成推广链接结果: {}", JSON.toJSONString(generateResponse));

        // 7. 组装返回数据
        RebateGoodsDto rebateGoodsDto = new RebateGoodsDto();
        rebateGoodsDto.setClickURL(generateResponse.getMobileShortUrl());
        rebateGoodsDto.setOriginalPrice(CurrencyConverter.convertCentsToYuan(goodsDetail.getMinGroupPrice()));
        //算总拥金
        Long promotionRate = goodsDetail.getPromotionRate();
        double totalRebatePrice = NumberUtil.round(rebateGoodsDto.getOriginalPrice() * promotionRate / 1000, 2).doubleValue();
        rebateGoodsDto.setTotalRebatePrice(totalRebatePrice);
        if (NumberUtil.compare(rebateGoodsDto.getTotalRebatePrice(), 0.0) <= 0) {
            throw new BaseException(noDiscountMsg);
        }

        double rate = UserRateUtils.calculateRebateAmount(rebateGoodsDto.getTotalRebatePrice());
        rebateGoodsDto.setRebatePrice(rate);
        rebateGoodsDto.setGoodsName(goodsDetail.getGoodsName());
        rebateGoodsDto.setGoodsId(goodsSign);
        return rebateGoodsDto;
    }


    public static void main(String[] args) {
//        String userId = "12345678";
//        RebateGoodsDto pddUrl = getPddUrl("https://mobile.yangkeduo.com/goods1.html?ps=WK8v86Edcy", userId, false);


//        generateUrl("E9D2L4u6vX9gRwXRweHeZFpjIzmvhALu_JQ4ATwUm3v");
//        LocalDateTime startTime = DateUtil.parseLocalDateTime("2024-11-18 16:38:00", "yyyy-MM-dd HH:mm:ss");
//        LocalDateTime endTime = DateUtil.parseLocalDateTime("2024-11-18 16:38:59", "yyyy-MM-dd HH:mm:ss");
//        List<PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem> orderList = getOrderListIncrement(startTime.atZone(ZoneId.systemDefault()).toEpochSecond()
//                , endTime.atZone(ZoneId.systemDefault()).toEpochSecond());
//        System.out.println(JSON.toJSONString(orderList));
    }

    /**
     * 增量查询订单列表
     * 秒
     */
    public List<PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem> getOrderListIncrement(Long startTime, Long endTime) {
        if (startTime == null || endTime == null) {
            throw new BaseException("startTime or endTime is null");
        }
        Integer pageNo = 1;
        final int pageSize = 100;
        List<PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem> result = new ArrayList<>();
        while (true) {
            PddDdkOrderListIncrementGetRequest request = new PddDdkOrderListIncrementGetRequest();
//        long entTime = System.currentTimeMillis() / 1000;
//        long startTime = entTime - 60 * 60;
            request.setStartUpdateTime(startTime);
            request.setEndUpdateTime(endTime);
            request.setPage(pageNo);
            request.setPageSize(pageSize);
            request.setQueryOrderType(1);
            request.setReturnCount(true);

            PddDdkOrderListIncrementGetResponse response = null;
            try {
                log.info("查询订单列表请求: {}", JSON.toJSONString(request));
                response = client.syncInvoke(request);
                log.info("查询订单列表结果: {}", JSON.toJSONString(response));
                List<PddDdkOrderListIncrementGetResponse.OrderListGetResponseOrderListItem> orderList = Optional.ofNullable(response)
                        .map(PddDdkOrderListIncrementGetResponse::getOrderListGetResponse)
                        .map(PddDdkOrderListIncrementGetResponse.OrderListGetResponse::getOrderList)
                        .orElse(null);
                if (CollUtil.isEmpty(orderList)) {
                    break;
                }
                result.addAll(orderList);
                //提前结束循环
                if (orderList.size() < pageSize) {
                    break;
                }
            } catch (Exception e) {
                log.error("调用接口失败", e);
                break;
            }
        }
        return result;
    }
} 