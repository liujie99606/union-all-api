package com.union.biz.convert;

import com.union.biz.model.UnionOrderDO;
import com.union.biz.vo.AppOrderRespVO;
import com.union.config.mybatis.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnionOrderConvert {

        UnionOrderConvert INSTANCE = Mappers.getMapper(UnionOrderConvert.class);

        List<AppOrderRespVO> convertToAppListVo(List<UnionOrderDO> bean);

       default PageResult<AppOrderRespVO> convertToPage(PageResult<UnionOrderDO> pageReqVO){
               return new PageResult<>(UnionOrderConvert.INSTANCE.convertToAppListVo(pageReqVO.getList()), pageReqVO.getTotal());
       }
}
