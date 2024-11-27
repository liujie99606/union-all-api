package com.union.biz.convert;

import com.union.biz.dto.UnionUserDto;
import com.union.biz.model.UnionUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
*
* @author lj
*/
@Mapper(componentModel = "spring")
public interface UnionUserConvert {

        UnionUserConvert INSTANCE = Mappers.getMapper(UnionUserConvert.class);

        UnionUserDto convert(UnionUserDO bean);

}
