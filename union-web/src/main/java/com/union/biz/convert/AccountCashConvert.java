package com.union.biz.convert;

import com.union.biz.model.UnionAccountCashDO;
import com.union.biz.vo.AppAccountCashRespVO;
import com.union.config.mybatis.PageResult;
import com.union.enums.AccountCashStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountCashConvert {

    AccountCashConvert INSTANCE = Mappers.getMapper(AccountCashConvert.class);

    AppAccountCashRespVO convertToAppVo(PageResult<UnionAccountCashDO> pageResult);

    List<AppAccountCashRespVO> convertToAppVoList(List<UnionAccountCashDO> list);

    AppAccountCashRespVO convertToAppVoList(UnionAccountCashDO list);

    default PageResult<AppAccountCashRespVO> convertToAppPage(PageResult<UnionAccountCashDO> pageResult) {
        List<AppAccountCashRespVO> respVOS = convertToAppVoList(pageResult.getList());
        return new PageResult<>(respVOS, pageResult.getTotal());
    }

    // 自定义转换方法
    default AccountCashStatusEnum mapIntegerToEnum(Integer cashStatus) {
        if (cashStatus == null) {
            return null;
        }
        return AccountCashStatusEnum.getEnum(cashStatus);
    }
}
