package com.union.biz.service.platform;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.request.query.GoodsReq;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.CommissionInfo;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.GoodsResp;
import com.jd.open.api.sdk.domain.kplunion.GoodsService.response.query.PriceInfo;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderRowReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowQueryResult;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.jd.open.api.sdk.domain.kplunion.promotionbysubunioni.PromotionService.request.get.PromotionCodeReq;
import com.jd.open.api.sdk.domain.kplunion.promotionbysubunioni.PromotionService.response.get.PromotionCodeResp;
import com.jd.open.api.sdk.request.kplunion.UnionOpenGoodsQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderRowQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenPromotionBysubunionidGetRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenGoodsQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderRowQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenPromotionBysubunionidGetResponse;
import com.union.base.exception.BaseException;
import com.union.biz.dto.RebateGoodsDto;
import com.union.utils.UserRateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class JdService {
    private static final String SERVER_URL = "https://api.jd.com/routerjson";
    private static final String appKey = "c0b127e386042fb9934cbef827a740f6";
    private static final String appSecret = "203b509d889a4502951e641bbd999cc9";
    private static final String siteId = "4101330087";
    private static final JdClient client;

    static {
        client = new DefaultJdClient(SERVER_URL, null, appKey, appSecret);
    }


    /**
     * 网站/APP/流量媒体来获取的推广链接，功能同宙斯接口的自定义链接转换、 APP领取代码接口通过商品链接、活动链接获取普通推广链接
     */
    public PromotionCodeResp getJdUrl(String url, String userId) {
        UnionOpenPromotionBysubunionidGetRequest request = new UnionOpenPromotionBysubunionidGetRequest();
        PromotionCodeReq promotionCodeReq = new PromotionCodeReq();
        promotionCodeReq.setMaterialId(url);
        promotionCodeReq.setSubUnionId(userId);
        promotionCodeReq.setCommand(1);
//        promotionCodeReq.setSceneId(1);

        request.setPromotionCodeReq(promotionCodeReq);
        request.setVersion("1.0");
        try {
            UnionOpenPromotionBysubunionidGetResponse response = client.execute(request);
            PromotionCodeResp result = response.getGetResult().getData();
            log.info("、活动链接获取普通推广链接,{}", JSON.toJSONString(result));
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GoodsResp getJdGoodsInfo(String skuId) {
        UnionOpenGoodsQueryRequest request = new UnionOpenGoodsQueryRequest();
        GoodsReq goodsReqDTO = new GoodsReq();
        Long[] arrays = {Long.valueOf(skuId)};
        goodsReqDTO.setSkuIds(arrays);
        request.setGoodsReqDTO(goodsReqDTO);
        request.setVersion("1.0");
        try {
            UnionOpenGoodsQueryResponse response = client.execute(request);
            GoodsResp[] result = response.getQueryResult().getData();
            log.info("通过关键词搜索商品,{}", JSON.toJSONString(result));
            if (ArrayUtil.isEmpty(result)) {
                return null;
            }
            return result[0];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void searchGoods(String keyword) {
        UnionOpenGoodsQueryRequest request = new UnionOpenGoodsQueryRequest();
        GoodsReq goodsReqDTO = new GoodsReq();
        Long[] arrays = {100107549365L};
        goodsReqDTO.setSkuIds(arrays);
        request.setGoodsReqDTO(goodsReqDTO);
        request.setVersion("1.0");
        try {
            UnionOpenGoodsQueryResponse response = client.execute(request);
            GoodsResp[] result = response.getQueryResult().getData();
            log.info("通过关键词搜索商品,{}", JSON.toJSONString(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List<OrderRowResp> getOrderRowInfo(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            throw new BaseException( "startTime or endTime is null");
        }

        List<OrderRowResp> result = new ArrayList<>();
        int pageNo = 1;
        final int pageSize = 500;

        while (true) {
            UnionOpenOrderRowQueryRequest request = new UnionOpenOrderRowQueryRequest();
            OrderRowReq orderReq = new OrderRowReq();

            orderReq.setPageIndex(pageNo);
            orderReq.setType(3);
            orderReq.setPageSize(pageSize);
            orderReq.setStartTime(startTime);
            orderReq.setEndTime(endTime);
            request.setOrderReq(orderReq);
            request.setVersion("1.0");
            UnionOpenOrderRowQueryResponse response;
            try {
                response = client.execute(request);
            } catch (Exception e) {
                log.error("通过关键词搜索商品", e);
                throw new BaseException( "同步订单失败");
            }
            if (response == null || response.getQueryResult() == null) {
                log.info("查询推广订单及佣金信息,行查询,响应为空");
                throw new BaseException( "同步订单失败");
            }

            OrderRowQueryResult queryResult = response.getQueryResult();
            if (queryResult.getCode() != 200) {
                log.info("查询推广订单及佣金信息,行查询,异常,{}", response);
                throw new BaseException( "同步订单失败");
            }

            OrderRowResp[] dataArr = queryResult.getData();
            if (ArrayUtil.isEmpty(dataArr)) {
                break;
            }
            result.addAll(Arrays.asList(dataArr));
            //提前结束循环
            if (dataArr.length < pageSize) {
                break;
            }
            pageNo++;
        }
        return result;
    }

    /**
     * 商品url得到商品详情
     */
    public RebateGoodsDto urlToDetail(String url, String goodsId, String userId) {
        GoodsResp goodsNode = getJdGoodsInfo(goodsId);
        if (goodsNode == null) {
            return null;
        }
        CommissionInfo commissionInfo = goodsNode.getCommissionInfo();
        //没有返利，直接返回
        if (commissionInfo == null) {
            return null;
        }
        //没有价格
        PriceInfo priceInfo = goodsNode.getPriceInfo();
        if (priceInfo == null) {
            return null;
        }

        Double commission = commissionInfo.getCommission();
        //没有返利，直接返回
        if (NumberUtil.compare(commission, 0.0) <= 0) {
            return null;
        }
        String goodsName = goodsNode.getSkuName();

        Double price = priceInfo.getPrice();

        RebateGoodsDto rebateGoodsDto = new RebateGoodsDto();
        rebateGoodsDto.setTotalRebatePrice(commission);
        PromotionCodeResp jsonNode = getJdUrl(url, userId);
        if (jsonNode == null) {
            return null;
        }
        String clickURL = jsonNode.getShortURL();
        rebateGoodsDto.setClickURL(clickURL);
        rebateGoodsDto.setOriginalPrice(price);
        //给用户的拥金
        double rate = UserRateUtils.calculateRebateAmount(commission);
        rebateGoodsDto.setRebatePrice(rate);
        rebateGoodsDto.setGoodsName(goodsName);
//        rebateGoodsDto.setAfterCouponPrice(price);

        return rebateGoodsDto;
    }


    public static void main(String[] args) {
        JdService jdService = new JdService();
//        PromotionGoodsResp[] jdGoodsInfo = jdService.getJdGoodsInfo("100093436245");
//        System.out.println(JSON.toJSONString(jdGoodsInfo));
//        //{"platformType":"JD","url":"https://3.cn/26m-PqTu"}
//        PromotionCodeResp promotionCodeResp = jdService.getJdUrl("https://3.cn/26m-PqTu");
//        System.out.println(JSON.toJSONString(promotionCodeResp));
        List<OrderRowResp> orderRowInfo = jdService.getOrderRowInfo("2024-10-28 13:23:00", "2024-10-28 14:05:00");
        System.out.println(JSON.toJSONString(orderRowInfo));
//        getJdUrl1("https://3.cn/26m-PqTu");
//        jdService.searchGoods("手链");


    }


}
