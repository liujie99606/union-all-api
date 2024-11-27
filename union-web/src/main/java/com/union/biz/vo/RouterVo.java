package com.union.biz.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class RouterVo {
    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "系统管理"}
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    private Meta meta;

    /**
     * 子路由
     */
    private List<RouterVo> children;

    @Getter
    @Setter
    @Builder
    public static class Meta implements Serializable {
        /**
         * 设置该路由在侧边栏和面包屑中展示的名字
         */
        private String title;

        /**
         * 设置该路由的图标，对应路径src/assets/icons/svg
         */
        private String icon;

        /**
         * 设置为true，则不会被 <keep-alive>缓存
         */
        private boolean noCache;

        /**
         * 内链地址（http(s)://开头）
         */
        private String link;
    }

}
