package com.union.controller.app;

import com.union.base.api.R;
import com.union.base.exception.BaseException;
import com.union.biz.convert.UnionOrderConvert;
import com.union.biz.facade.OrderSyncFacade;
import com.union.biz.model.UnionOrderDO;
import com.union.biz.service.UnionOrderService;
import com.union.biz.vo.AppOrderPageReqVO;
import com.union.biz.vo.AppOrderRespVO;
import com.union.config.mybatis.PageResult;
import com.union.security.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


@Api(tags = "返利联盟 - 订单管理")
@RestController
@Slf4j
@RequestMapping("/union/order")
@Validated
public class AppOrderController {

    @Resource
    private UnionOrderService unionOrderService;

    @Resource
    private OrderSyncFacade orderSyncFacade;

    private final static String KEY = "56c0100cbb1448efb96c69c55921ab50";

    @PostMapping("/page")
    @Operation(summary = "订单列表")
    public R<PageResult<AppOrderRespVO>> page(@Valid @RequestBody AppOrderPageReqVO pageReqVO) {
        pageReqVO.setUserId(SecurityUtil.getUserIdAsLong());
        PageResult<UnionOrderDO> pageResult = unionOrderService.getPageList(pageReqVO);
        return R.ok(UnionOrderConvert.INSTANCE.convertToPage(pageResult));
    }

    /**
     * 同步京东订单
     * 2024-10-15 12:00:00
     * 2024-10-15 12:59:59
     */
    @GetMapping("/sync/jd")
    @Operation(summary = "同步京东订单")
    public R<Boolean> syncJdOrders(@RequestParam String startTime,
                                   @RequestParam String endTime,
                                   @RequestParam String key) {
        // 验证key
        if (!KEY.equals(key)) {
            throw new BaseException("无效的密钥");
        }
        orderSyncFacade.syncJdOrders(startTime, endTime);

        return R.ok(true);
    }

    /**
     * 同步淘宝订单
     */
    @GetMapping("/sync/taobao")
    @Operation(summary = "同步淘宝订单")
    public R<Boolean> syncTaobaoOrders(@RequestParam String startTime,
                                       @RequestParam String endTime,
                                       @RequestParam String key) {
        // 验证key
        if (!KEY.equals(key)) {
            throw new BaseException("无效的密钥");
        }
        orderSyncFacade.taobaoOrderSync(startTime, endTime);
        return R.ok(true);
    }

    /**
     * 同步拼多多订单
     */
    @GetMapping("/sync/pdd")
    @Operation(summary = "同步拼多多订单")
    public R<Boolean> syncPddOrders(@RequestParam String startTime,
                                    @RequestParam String endTime,
                                    @RequestParam String key) {
        // 验证key
        if (!KEY.equals(key)) {
            throw new BaseException("无效的密钥");
        }
        orderSyncFacade.syncPddOrders(startTime, endTime);
        return R.ok(true);
    }


}
