package com.union.biz.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.union.biz.dto.CreateUserDto;
import com.union.biz.mapper.UnionQrcodeMapper;
import com.union.biz.mapper.UnionUserMapper;
import com.union.biz.model.UnionUserDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author lj
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnionUserService extends ServiceImpl<UnionUserMapper, UnionUserDO> {

    private final UnionUserMapper unionUserMapper;
    private final UnionQrcodeMapper unionQrcodeMapper;

    /**
     * 获取或者创建一个用户
     */
    public UnionUserDO getOrCreateUser(CreateUserDto dto) {
        String openId = dto.getOpenId();
        Integer sourceType = dto.getSourceType();

        // 优先查询用户
        UnionUserDO user = unionUserMapper.selectOne(UnionUserDO::getOpenId, openId,
                UnionUserDO::getSourceType, sourceType);
        if (user != null) {
            return user;
        }

        // 创建用户
        UnionUserDO createUser = UnionUserDO.builder()
                .openId(openId)
                .sourceType(sourceType)
                .build();
        //查询邀请者
        Optional.ofNullable(dto.getQrScene())
                .filter(StrUtil::isNotBlank)
                .map(qrScene -> qrScene.replace("qrscene_", ""))
                .filter(StrUtil::isNotBlank)
                .map(unionQrcodeMapper::selectById)
                .ifPresent(qrcodeDO -> createUser.setInviterUserId(qrcodeDO.getUserId()));
        // 设置昵称
        String uuid = UUID.fastUUID().toString(true);
        createUser.setNickname("user" + uuid.substring(0, 8));
        createUser.setCreator("system");
        unionUserMapper.insert(createUser);
        return createUser;
    }

}