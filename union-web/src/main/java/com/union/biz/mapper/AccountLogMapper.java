package com.union.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.union.biz.model.UnionAccountLogDO;
import com.union.biz.vo.AccountLogPageReqVO;
import com.union.biz.vo.AppAccountLogPageReqVO;
import com.union.config.mybatis.BaseMapperX;
import com.union.config.mybatis.PageResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountLogMapper extends BaseMapperX<UnionAccountLogDO> {


    default PageResult<UnionAccountLogDO> selectPageList(AccountLogPageReqVO reqVO){
        return selectPage(reqVO, new LambdaQueryWrapper<UnionAccountLogDO>()
                .eq(UnionAccountLogDO::getAccountId, reqVO.getAccountId())
                .orderByDesc(UnionAccountLogDO::getId));
    }

    default PageResult<UnionAccountLogDO> selectPageList(AppAccountLogPageReqVO reqVO){
        return selectPage(reqVO, new LambdaQueryWrapper<UnionAccountLogDO>()
                .eq(UnionAccountLogDO::getUserId, reqVO.getUserId())
                .orderByDesc(UnionAccountLogDO::getId));
    }
}
