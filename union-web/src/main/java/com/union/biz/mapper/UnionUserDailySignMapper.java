package com.union.biz.mapper;

import com.union.biz.model.UnionUserDailySignDO;
import com.union.config.mybatis.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface UnionUserDailySignMapper extends BaseMapperX<UnionUserDailySignDO> {

    default UnionUserDailySignDO selectByUserIdAndSignDate(Long userId, LocalDate signDate) {
        return selectOne(UnionUserDailySignDO::getUserId, userId,
                UnionUserDailySignDO::getSignDate, signDate);
    }


}