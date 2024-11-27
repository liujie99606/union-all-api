package com.union.biz.convert;

import com.union.biz.dto.AccountReqDTO;
import com.union.biz.model.UnionAccountDO;
import com.union.biz.model.UnionAccountLogDO;
import com.union.biz.vo.AccountLogRespVO;
import com.union.biz.vo.AppAccountLogRespVO;
import com.union.config.mybatis.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountLogConvert {

        AccountLogConvert INSTANCE = Mappers.getMapper(AccountLogConvert.class);

        List<AccountLogRespVO> convertToVoList(List<UnionAccountLogDO> list);

        List<AppAccountLogRespVO> convertToAppVoList(List<UnionAccountLogDO> list);

        default PageResult<AccountLogRespVO> convertToPage(PageResult<UnionAccountLogDO> pageResult) {
                List<AccountLogRespVO> respVOS = convertToVoList(pageResult.getList());
                return new PageResult<>(respVOS, pageResult.getTotal());
        }

        default PageResult<AppAccountLogRespVO> convertToAppPage(PageResult<UnionAccountLogDO> pageResult) {
                List<AppAccountLogRespVO> respVOS = convertToAppVoList(pageResult.getList());
                return new PageResult<>(respVOS, pageResult.getTotal());
        }


        default UnionAccountLogDO convertToIncomeDo(AccountReqDTO req, UnionAccountDO account){

                UnionAccountLogDO log = new UnionAccountLogDO();
                log.setTotalOld(account.getTotal());
                log.setBalanceOld(account.getBalance());
                log.setIncomeOld(account.getIncome());
                log.setIncomeNew(account.getIncome().add(req.getTradeAmount()));
                log.setTotal(account.getTotal().add(req.getTradeAmount()));
                log.setBalanceNew(account.getBalance().add(req.getTradeAmount()));
                log.setAccountId(account.getId());
                // log.setLogType(LogTypeEnum.ACTIVITY_INCOME.getCode());
                log.setAmount(req.getTradeAmount());
                log.setIncome(req.getTradeAmount());
                log.setBalance(req.getTradeAmount());
                log.setTargetId(req.getTargetId());
                log.setTargetCode("");
                log.setUserId(req.getUserId());
                log.setTargetName(req.getTargetType().getMessage());
                log.setTargetType(req.getTargetType().name());
                log.setSummary(req.getSummary());
                log.setExpend(BigDecimal.ZERO);
                log.setExpendNew(account.getExpend());
                log.setExpendOld(account.getExpend());
                log.setFrost(BigDecimal.ZERO);
                log.setFrostNew(account.getFrost());
                log.setFrostOld(account.getFrost());
                return log;
        }

        default UnionAccountLogDO convertToExpendDo(AccountReqDTO req, UnionAccountDO account){

                UnionAccountLogDO log = new UnionAccountLogDO();
                log.setAccountId(account.getId());
                //log.setLogType(LogTypeEnum.EXPEND_BLACKLIST.getCode());
                log.setAmount(req.getTradeAmount());
                log.setTargetName(req.getTargetType().getMessage());
                log.setTargetType(req.getTargetType().name());

                log.setTotalOld(account.getTotal());
                log.setTotal(account.getTotal().subtract(req.getTradeAmount()));
                log.setBalanceOld(account.getBalance());
                log.setBalanceNew(account.getBalance().subtract(req.getTradeAmount()));
                log.setBalance(req.getTradeAmount());
                log.setIncome(BigDecimal.ZERO);
                log.setIncomeOld(account.getIncome());
                log.setIncomeNew(account.getIncome());
                log.setExpend(req.getTradeAmount());
                log.setExpendNew(account.getExpend().add(req.getTradeAmount()));
                log.setExpendOld(account.getExpend());
                log.setFrost(BigDecimal.ZERO);
                log.setFrostNew(account.getFrost());
                log.setFrostOld(account.getFrost());
                log.setSummary(req.getSummary());
                return log;
        }
}
