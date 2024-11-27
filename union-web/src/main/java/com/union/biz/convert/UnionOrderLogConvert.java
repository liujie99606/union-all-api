package com.union.biz.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UnionOrderLogConvert {

        UnionOrderLogConvert INSTANCE = Mappers.getMapper(UnionOrderLogConvert.class);


}