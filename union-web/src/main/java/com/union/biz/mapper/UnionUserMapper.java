package com.union.biz.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.union.biz.model.UnionUserDO;
import com.union.config.mybatis.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnionUserMapper extends BaseMapperX<UnionUserDO> {

    default Long countInviteUsers(Long userId) {
        return selectCount(
                Wrappers.lambdaQuery(UnionUserDO.class)
                        .eq(UnionUserDO::getInviterUserId, userId)
                        .eq(UnionUserDO::getDeleted, false)
        );
    }

}