package com.union.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.union.biz.model.UnionOrderDO;
import com.union.biz.vo.AppOrderPageReqVO;
import com.union.config.mybatis.BaseMapperX;
import com.union.config.mybatis.PageResult;
import com.union.enums.OrderStatusEnum;
import com.union.enums.SettleStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UnionOrderMapper extends BaseMapperX<UnionOrderDO> {

    default UnionOrderDO findFirstByOrderCodeAndPlatformId(String orderCode, String platformId) {
        return selectOne(
                Wrappers.lambdaQuery(UnionOrderDO.class)
                        .eq(UnionOrderDO::getOrderCode, orderCode)
                        .eq(UnionOrderDO::getPlatformId, platformId)
                        .last(" limit 1")
        );
    }

    default List<UnionOrderDO> selectAllWaitSettleOrder() {

        return selectList(new LambdaQueryWrapper<UnionOrderDO>()
                .eq(UnionOrderDO::getOrderStatus, OrderStatusEnum.SUCCESS.getCode())
                .eq(UnionOrderDO::getSettleStatus, SettleStatusEnum.WAIT_SETTLE.getCode())
        );

    }


    default PageResult<UnionOrderDO> selectPageList(AppOrderPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapper<UnionOrderDO>()
                .eq(UnionOrderDO::getUserId, pageReqVO.getUserId())
                .eq(UnionOrderDO::getOrderStatus, pageReqVO.getStatus())
                .between(UnionOrderDO::getCreateTime, pageReqVO.getBeginTime(), pageReqVO.getEndTime())
        );
    }
}
