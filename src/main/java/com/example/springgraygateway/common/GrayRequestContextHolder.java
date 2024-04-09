package com.example.springgraygateway.common;

/**
 * 请求灰度标记存储 上下文
 *
 * @author yinyong
 * @version 1.0
 * @classname GrayRequestContextHolder
 * @date 2024/4/6 15:23
 */
public class GrayRequestContextHolder {

    private static final ThreadLocal<Boolean> grayTagHolder = new ThreadLocal<>();

    public static void setGrayTag(boolean grayTag) {
        grayTagHolder.set(grayTag);
    }

    public static Boolean getGrayTag() {
        return grayTagHolder.get();
    }

    public static void clear() {
        grayTagHolder.remove();
    }
}
