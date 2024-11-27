/**
 *
 */
package com.union.base.context;

/**
 * @author huangxm
 *
 */
public class WebContext {

    private static ThreadLocal<Integer> _page = new ThreadLocal<>();

    private static ThreadLocal<Integer> _size = new ThreadLocal<>();


    //小程序或公众号APPid
    private static ThreadLocal<String> _appId = new ThreadLocal<>();

    //企业微信应用iD
    private static ThreadLocal<Integer> _agentId = new ThreadLocal<>();


    public static Integer getPage() {
        return _page.get();
    }

    public static void setPage(Integer page) {
        _page.set(page);
    }
    public static void removePage() { _size.remove(); }


    public static Integer getSize() {

        return _size.get() == null ? 10 : _size.get();
    }

    public static void setSize(Integer size) {
        _size.set(size);
    }

    public static void removeSize() {
        _page.remove();
    }


    public static Integer getAgentId() {
        return _agentId.get();
    }

    public static void setAgentId(Integer agentId) {
        _agentId.set(agentId);
    }

    public static void removeAgentId() {
        _agentId.remove();
    }


    public static String getAppId() {
        return _appId.get();
    }

    public static void setAppId(String appId) {
        _appId.set(appId);
    }

    public static void removeAppId() {
        _appId.remove();
    }

}
