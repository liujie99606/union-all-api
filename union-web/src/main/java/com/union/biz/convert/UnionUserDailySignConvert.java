package com.union.biz.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
*
* @author lj
*/
@Mapper(componentModel = "spring")
public interface UnionUserDailySignConvert {

        UnionUserDailySignConvert INSTANCE = Mappers.getMapper(UnionUserDailySignConvert.class);


}