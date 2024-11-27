package com.union.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.union.biz.model.UnionAccountCashDO;
import com.union.biz.vo.AppAccountCashPageReqVO;
import com.union.config.mybatis.BaseMapperX;
import com.union.config.mybatis.PageResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountCashMapper extends BaseMapperX<UnionAccountCashDO> {

    default PageResult<UnionAccountCashDO> selectPageList(AppAccountCashPageReqVO reqVO){

        return selectPage(reqVO, new LambdaQueryWrapper<UnionAccountCashDO>()
                .eq(UnionAccountCashDO::getUserId, reqVO.getUserId())
                .eq(UnionAccountCashDO::getCashStatus, reqVO.getStatus())
                .orderByDesc(UnionAccountCashDO::getCreateTime));
    }

}
