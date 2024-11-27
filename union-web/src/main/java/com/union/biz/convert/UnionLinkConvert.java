package com.union.biz.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UnionLinkConvert {

        UnionLinkConvert INSTANCE = Mappers.getMapper(UnionLinkConvert.class);


}