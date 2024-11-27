package com.union.biz.service.platform;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.taobao.top.DefaultTopApiClient;
import com.taobao.top.TopApiClient;
import com.taobao.top.ability375.Ability375;
import com.taobao.top.ability375.domain.TaobaoTbkTpwdCreateMapData;
import com.taobao.top.ability375.request.TaobaoTbkTpwdCreateRequest;
import com.taobao.top.ability375.response.TaobaoTbkTpwdCreateResponse;
import com.taobao.top.ability414.Ability414;
import com.taobao.top.ability414.domain.TaobaoTbkOrderDetailsGetOrderPage;
import com.taobao.top.ability414.domain.TaobaoTbkOrderDetailsGetPublisherOrderDto;
import com.taobao.top.ability414.request.TaobaoTbkOrderDetailsGetRequest;
import com.taobao.top.ability414.response.TaobaoTbkOrderDetailsGetResponse;
import com.taobao.top.defaultability.Defaultability;
import com.taobao.top.defaultability.domain.*;
import com.taobao.top.defaultability.request.TaobaoTbkDgMaterialOptionalUpgradeRequest;
import com.taobao.top.defaultability.response.TaobaoTbkDgMaterialOptionalUpgradeResponse;
import com.union.base.exception.BaseException;
import com.union.biz.dto.RebateGoodsDto;
import com.union.utils.TextUtils;
import com.union.utils.UserRateUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TaobaoUtils {

    private static final String appkey = "34859404";
    private static final String secret = "df61f1619373a8faaf166758e41f0785";
    private static final String url = "https://eco.taobao.com/router/rest";

    private final static TopApiClient client = new DefaultTopApiClient(appkey, secret, url);

    private TaobaoUtils() {
        // 私有构造函数，防止实例化
    }


    /**
     * 淘宝客-推广者-物料搜索升级版
     */
    public static TaobaoTbkDgMaterialOptionalUpgradeMapData taobaoTbkDgMaterialOptionalUpgrade(String q) {
        Defaultability apiPackage = new Defaultability(client);
        TaobaoTbkDgMaterialOptionalUpgradeRequest request = new TaobaoTbkDgMaterialOptionalUpgradeRequest();
        request.setGetTopnRate(0L);
        request.setAdzoneId(115794850477L);
        request.setPageNo(1L);
        request.setPageSize(100L);
        request.setQ(q);
        request.setSort("tk_rate_des_des");
        //商品筛选-淘客收入比率下限(商品佣金比率+补贴比率)。如：1234表示12.34%
        request.setStartTkRate(1L);

        TaobaoTbkDgMaterialOptionalUpgradeResponse response = null;
        try {
            response = apiPackage.taobaoTbkDgMaterialOptionalUpgrade(request);
        } catch (IOException e) {
            log.error("调用接口失败", e);
            return null;
        }
        if (!response.isSuccess()) {
            log.info("调用接口失败,{}", response.getSubMsg());
        }
        List<TaobaoTbkDgMaterialOptionalUpgradeMapData> resultList = response.getResultList();
        if (CollUtil.isEmpty(resultList)) {
            return null;
        }
        //按价格排序，如果加个一样，按拥金最高的排序
        resultList = resultList.stream()
                .filter(item -> StrUtil.equals(q, item.getItemBasicInfo().getTitle()))
                .sorted(Comparator.comparing((TaobaoTbkDgMaterialOptionalUpgradeMapData s) -> s.getPricePromotionInfo().getFinalPromotionPrice())
                        .thenComparing(Comparator.comparing((TaobaoTbkDgMaterialOptionalUpgradeMapData s) -> s.getPublishInfo().getIncomeInfo().getCommissionAmount()).reversed())
                        .thenComparing(Comparator.comparing((TaobaoTbkDgMaterialOptionalUpgradeMapData s) -> s.getItemBasicInfo().getAnnualVol()).reversed())
                )
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(resultList)) {
            return null;
        }
        return resultList.get(0);
    }

    /**
     * 淘宝客-公用-淘口令生成
     */
    public static TaobaoTbkTpwdCreateMapData getTKL(String url) {
        Ability375 apiPackage = new Ability375(client);

        TaobaoTbkTpwdCreateRequest request = new TaobaoTbkTpwdCreateRequest();
        request.setUrl(url);
        request.setText("test123");

        TaobaoTbkTpwdCreateResponse response = null;
        try {
            response = apiPackage.taobaoTbkTpwdCreate(request);
        } catch (IOException e) {
            log.error("调用接口失败", e);
            return null;
        }
        if (!response.isSuccess()) {
            log.info("调用接口失败,{}", response.getSubMsg());
        }
        return response.getData();
    }



    public static RebateGoodsDto getTKLByGoodsName(String taobaoGoodsName) {
        taobaoGoodsName = TextUtils.getTaobaoGoodsName(taobaoGoodsName);
        if (StrUtil.isBlank(taobaoGoodsName)) {
            log.info("商品名称获取为空");
            return null;
        }
        log.info("商品名称:{}", taobaoGoodsName);
        TaobaoTbkDgMaterialOptionalUpgradeMapData upgradeMapData = taobaoTbkDgMaterialOptionalUpgrade(taobaoGoodsName);
        log.info("商品信息:{}", JSON.toJSONString(upgradeMapData));
        if (upgradeMapData == null) {
            return null;
        }
        RebateGoodsDto goodsInfo = new RebateGoodsDto();
        TaobaoTbkDgMaterialOptionalUpgradePublishInfo publishInfo = upgradeMapData.getPublishInfo();
        if (publishInfo != null) {
            String clickUrl = publishInfo.getClickUrl();
            clickUrl = "https:" + clickUrl;
            TaobaoTbkTpwdCreateMapData tkl = getTKL(clickUrl);
            log.info("商品链接:{}", JSON.toJSONString(tkl));
            if (tkl == null) {
                return null;
            }
            goodsInfo.setClickURL(tkl.getModel());
            goodsInfo.setGoodsId(TextUtils.getTaobaoGoodsId(upgradeMapData.getItemId()));
            TaobaoTbkDgMaterialOptionalUpgradePromotionInfoMapData priceInfo = upgradeMapData.getPricePromotionInfo();
            if (priceInfo != null) {
                String price = priceInfo.getFinalPromotionPrice();
                if (price == null) {
                    price = priceInfo.getZkFinalPrice();
                }
                if (price == null) {
                    price = priceInfo.getReservePrice();
                }
                if (price != null) {
                    goodsInfo.setOriginalPrice(Double.valueOf(price));
                }
            }

            Optional.ofNullable(upgradeMapData.getPublishInfo())
                    .map(TaobaoTbkDgMaterialOptionalUpgradePublishInfo::getIncomeInfo)
                    .map(TaobaoTbkDgMaterialOptionalUpgradeFinalIncomeInfo::getCommissionAmount)
                    .ifPresent(amount -> {
                        goodsInfo.setTotalRebatePrice(Double.valueOf(amount));
                        double rate = UserRateUtils.calculateRebateAmount(goodsInfo.getTotalRebatePrice());
                        goodsInfo.setRebatePrice(rate);
                    });

            Optional.ofNullable(upgradeMapData.getItemBasicInfo())
                    .map(TaobaoTbkDgMaterialOptionalUpgradeBasicMapData::getTitle)
                    .ifPresent(goodsInfo::setGoodsName);
            return goodsInfo;
        }
        return null;
    }

    public static List<TaobaoTbkOrderDetailsGetPublisherOrderDto> taobaoTbkOrderDetailsGet(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            throw new BaseException( "startTime or endTime is null");
        }
        Ability414 apiPackage = new Ability414(client);

        List<TaobaoTbkOrderDetailsGetPublisherOrderDto> result = new ArrayList<>();
        long pageNo = 1L;
        final long pageSize = 500L;
        while (true) {
            TaobaoTbkOrderDetailsGetRequest request = new TaobaoTbkOrderDetailsGetRequest();
            //查询时间类型，1：按照订单淘客创建时间查询，2:按照订单淘客付款时间查询，3:按照订单淘客结算时间查询，4:按照订单更新时间；
            request.setQueryType(4L);
//        request.setPositionIndex("2222_334666");
            request.setPageSize(pageSize);
            request.setEndTime(endTime);
            request.setStartTime(startTime);
            request.setPageNo(pageNo);

            TaobaoTbkOrderDetailsGetResponse response;
            try {
                response = apiPackage.taobaoTbkOrderDetailsGet(request);
                if (!response.isSuccess()) {
                    log.info("调用接口失败,{}", response);
                    break;
                }
                TaobaoTbkOrderDetailsGetOrderPage orderPage = response.getData();
                if (orderPage == null) {
                    break;
                }
                List<TaobaoTbkOrderDetailsGetPublisherOrderDto> results = orderPage.getResults();
                if (CollUtil.isEmpty(results)) {
                    break;
                }
                result.addAll(results);
                //提前结束循环
                if (!BooleanUtil.isTrue(response.getData().getHasNext())) {
                    break;
                }
            } catch (IOException e) {
                log.error("淘宝客-推广者-所有订单查询", e);
                break;
            }
            pageNo++;
        }
        return result;
    }


    public static void main(String[] args) {
//        RebateGoodsDto rebateGoodsDto = getTKLByGoodsName("【淘宝】http://e.tb.cn/h.T0HvP5B92ZsFN5t?tk=isKP3rHWI0H HU9196 「【百补】花王乐而雅卫生巾零触感特薄超长安心夜用组合姨妈巾32片」");
//        log.info("tklByGoodsName:{}", JSON.toJSONString(rebateGoodsDto));
//        TaobaoService taobaoUtils = new TaobaoService();
        List<TaobaoTbkOrderDetailsGetPublisherOrderDto> result = TaobaoUtils.taobaoTbkOrderDetailsGet("2024-11-15 12:57:00", "2024-11-15 12:59:59");
        System.out.println(JSON.toJSONString(result));

    }

}
