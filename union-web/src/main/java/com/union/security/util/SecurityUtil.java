package com.union.security.util;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.stp.StpUtil;
import com.union.base.util.StrUtil;
import com.union.constant.CommonConstants;
import com.union.security.entity.AuthUser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class SecurityUtil {

    private final String USER_KEY = "union";

    public void setUser(String userId, AuthUser authUser) {
        StpUtil.login(userId);
        StpUtil.getTokenSession().set(USER_KEY, authUser);
    }

    public AuthUser getUser() {
        AuthUser authUser = (AuthUser) StpUtil.getTokenSession().get(USER_KEY);
        if (authUser == null) {
            throw new NotLoginException("未登录", NotLoginException.INVALID_TOKEN, NotLoginException.INVALID_TOKEN_MESSAGE);
        }
        return authUser;
    }

    public String getUserName() {

        return getUser().getUserName();
    }

    public Integer getDeptId() {
        return getUser().getDeptId();
    }

    /**
     * 获取corpId
     *
     * @return
     */
    public String getCorpId() {
        return getUser().getCorpId();
    }

    public String getUserId() {
        return StpUtil.getLoginIdAsString();
    }

    public String getUserIdOrNull() {
        try {
            return Optional.ofNullable(StpUtil.getLoginIdDefaultNull())
                    .map(String::valueOf)
                    .orElse(null);
        } catch (NotWebContextException e) {
            log.warn("获取用户id失败,{}", e.getMessage());
            return null;
        }
    }

    public String getUserIdOrAnonymous() {
        try {
            return Optional.ofNullable(StpUtil.getLoginIdDefaultNull())
                    .map(String::valueOf)
                    .orElse("anonymous");
        } catch (NotWebContextException e) {
            log.warn("获取用户id失败,{}", e.getMessage());
            return "anonymous";
        }
    }


    public Set<String> getRoles() {
        return getUser().getRoles();
    }

    /**
     * 获取所有角色id
     *
     * @return
     */
    public List<String> getRoleList() {
        return getRoles().stream()
                .filter(roleId -> StrUtil.startWith(roleId, CommonConstants.ROLE))
                .map(roleId -> StrUtil.removePrefix(roleId, CommonConstants.ROLE))
                .collect(Collectors.toList());
    }

    public Set<String> getPermissions() {
        return getUser().getPermissions();
    }
}
