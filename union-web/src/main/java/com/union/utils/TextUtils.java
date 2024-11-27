package com.union.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.union.enums.TextMessageTypeEnum;

import java.util.List;


public class TextUtils {


    /**
     * 根据文本获取平台类型
     */
    public static TextMessageTypeEnum getPlatformType(String text) {
        if (StrUtil.isBlank(text)) {
            return TextMessageTypeEnum.UNKNOWN;
        }
        if (StrUtil.equals("余额", text)) {
            return TextMessageTypeEnum.BALANCE;
        }
        if (StrUtil.equals("提现", text)) {
            return TextMessageTypeEnum.WITHDRAW;
        }
        if (StrUtil.equals("签到", text)) {
            return TextMessageTypeEnum.SIGN_IN;
        }
        if (StrUtil.equals("所有功能", text)) {
            return TextMessageTypeEnum.ALL;
        }
        if (StrUtil.equals("邀请码", text)) {
            return TextMessageTypeEnum.INVITE_CODE;
        }
        if (StrUtil.equals("邀请统计", text)) {
            return TextMessageTypeEnum.INVITE_STATISTICS;
        }
        if (StrUtil.equals("个人中心", text)) {
            return TextMessageTypeEnum.PERSONAL_CENTER;
        }
        if (OrderUtils.isOrder(text)) {
            return TextMessageTypeEnum.ORDER_QUERY;
        }

        //【京东】https://3.cn/26mHh-QN「华为 手环9 标准版 智能手环 星空黑 」
        if (text.contains("【京东】http") || text.contains(".jd.com")) {
            return TextMessageTypeEnum.JD;
        }

        // 【淘宝】100%买家好评 http://e.tb.cn/h.gu0rBec03o5t4ub?tk=fwPZ3kfnAkY MF3543
        if (text.contains("【淘宝】") && text.contains(".tb.")) {
            return TextMessageTypeEnum.TAO_BAO;
        }

        // 1.00 H@v.Fu Tyt:/ 02/09 【抖音商城】https://v.douyin.com/iBf7ybmC/ 【HN怀化万达NK】耐克男子空军一号白灰低帮时尚板鞋 FJ4146100
        if (text.contains("【抖音商城】http") && text.contains(".douyin.")) {
            return TextMessageTypeEnum.DOU_YIN;
        }
        //https://mobile.yangkeduo.com/goods.html?ps=9cxDfrvRb2
        if (text.contains(".yangkeduo.")) {
            return TextMessageTypeEnum.PDD;
        }
        return TextMessageTypeEnum.UNKNOWN;
    }

    /**
     * 获取淘宝商品名称
     */
    public static String getTaobaoGoodsName(String text) {
        String regex = "「(.*?)」";
        List<String> result = ReUtil.findAllGroup1(regex, text);
        if (CollUtil.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * 提取文本中url
     */
    public static String extractUrl(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        // 提取文本中的URL
        return extractUrls(text);
    }

    /**
     * 使用 Hutool 的方法提取文本中的URL
     */
    public static String extractUrls(String text) {
        // 使用 ReUtil 根据正则表达式提取URL
        return ReUtil.get(Validator.URL_HTTP, text, 0);
    }

    /**
     * 获取京东商品id
     */
    public static String getJdProductId(String text) {
        String shortUrl = extractUrl(text);
        if (StrUtil.isBlank(shortUrl)) {
            return null;
        }
        if (shortUrl.contains("jd.com")) {
            String pattern = "/(\\d+)\\.html"; // 匹配数字并确保链接以".html"结尾

            // 使用Hutool的ReUtil提取商品ID
            return ReUtil.get(pattern, shortUrl, 1);
        }

        // 获取重定向后的URL
        String longUrl = HttpUtil.createGet(shortUrl)
                .execute()
                .header("Location");
        System.out.println("重定向后的URL: " + longUrl);

        // 如果没有重定向，则返回原链接
        if (longUrl == null) {
            longUrl = shortUrl;
        }

        // 从长链接中提取商品ID
        // 正则表达式提取商品ID
        String pattern = "product/(\\d+)";
        return ReUtil.get(pattern, longUrl, 1);
    }

    public static String getGoodsSign(String url) {
        UrlBuilder builder = UrlBuilder.ofHttp(url, CharsetUtil.CHARSET_UTF_8);
        UrlQuery query = builder.getQuery();
        return String.valueOf(query.get("goods_sign"));
    }

    /**
     * 处理商品名称
     * 如果小于10位，直接返回
     * 如果大于10位，前后各取5位，中间拼接****
     *
     * @param goodsName 商品名称
     * @return 处理后的商品名称
     */
    public static String handleGoodsName(String goodsName) {
        if (StrUtil.isBlank(goodsName)) {
            return goodsName;
        }
        if (goodsName.length() <= 10) {
            return goodsName;
        }
        return goodsName.substring(0, 5) + "****" + goodsName.substring(goodsName.length() - 5);
    }

    /**
     * 获取淘宝商品id
     *
     * @param text 淘宝商品链接或商品id字符串
     * @return 商品id
     */
    public static String getTaobaoGoodsId(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        // 如果包含"-"，取最后一段
        if (text.contains("-")) {
            return text.substring(text.lastIndexOf("-") + 1);
        }
        return text;
    }

    public static void main(String[] args) {
//        String jdProductId = getJdProductId("https://item.jd.com/10089075878580.html");
//        System.out.println(jdProductId);
        System.out.println(handleGoodsName("盐津铺子鹌鹑蛋混合口味400g 休闲食品富硒卤蛋送礼零食大礼包约60颗"));
        String goodsId = getTaobaoGoodsId("7vtYU5bBMg0e9tnawiMtV-vBXWaP5cBdGXO0e8H0");
        System.out.println(goodsId); // 输出: vBXWaP5cBdGXO0e8H0
    }
}
