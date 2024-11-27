package com.union.biz.convert;

import com.union.biz.model.UnionAccountDO;
import com.union.biz.vo.AccountRespVO;
import com.union.biz.vo.AppAccountRespVO;
import com.union.config.mybatis.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountConvert {

    AccountConvert INSTANCE = Mappers.getMapper(AccountConvert.class);

    AppAccountRespVO convertToAppVo(UnionAccountDO unionAccountDO);

    List<AccountRespVO> convertToVoList(List<UnionAccountDO> list);

    default PageResult<AccountRespVO> convertToPage(PageResult<UnionAccountDO> pageResult) {
        List<AccountRespVO> respVOS = convertToVoList(pageResult.getList());
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

}
