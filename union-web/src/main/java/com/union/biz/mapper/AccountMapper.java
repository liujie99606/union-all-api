package com.union.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.vo.AccountPageReqVO;
import com.union.config.mybatis.BaseMapperX;
import com.union.config.mybatis.PageResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapperX<UnionAccountDO> {

    default UnionAccountDO findFirstByUserId(Long userId) {
        return selectOne(
                Wrappers.lambdaQuery(UnionAccountDO.class)
                        .eq(UnionAccountDO::getUserId, userId)
                        .eq(UnionAccountDO::getDeleted, 0)
                        .last("LIMIT 1")
        );
    }

    default PageResult<UnionAccountDO> selectPageList(AccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapper<UnionAccountDO>()
                .like(UnionAccountDO::getId, reqVO.getUsername())
                .orderByDesc(UnionAccountDO::getId));
    }
}
