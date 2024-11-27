package com.union.biz.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
*
* @author lj
*/
@Mapper(componentModel = "spring")
public interface UnionQrcodeConvert {

        UnionQrcodeConvert INSTANCE = Mappers.getMapper(UnionQrcodeConvert.class);


}