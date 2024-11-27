package com.union.biz.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.union.biz.mapper.UnionOrderLogMapper;
import com.union.biz.model.UnionOrderLogDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
*
* @author lj
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class UnionOrderLogService extends ServiceImpl<UnionOrderLogMapper, UnionOrderLogDO>{

    private final UnionOrderLogMapper unionOrderLogMapper;


}