package com.union.data.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.union.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class EsenMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        String userId = SecurityUtil.getUserIdOrAnonymous();
        this.strictInsertFill(metaObject, "createdBy", String.class, userId);
        this.strictInsertFill(metaObject, "lastModifiedBy", String.class, userId);

        Date now = new Date();
        this.strictInsertFill(metaObject, "createdTime", Date.class, now);
        this.strictInsertFill(metaObject, "lastModifiedTime", Date.class, now);
        this.strictInsertFill(metaObject, "delFlag", Boolean.class, false);
        this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
        this.strictInsertFill(metaObject, "storeStatus", String.class, "ENABLE");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "lastModifiedTime", Date.class, new Date());
        String userId = SecurityUtil.getUserIdOrNull();
        this.strictUpdateFill(metaObject, "lastModifiedBy", String.class, userId);
    }

}
