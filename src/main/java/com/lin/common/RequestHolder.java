package com.lin.common;

import com.lin.model.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 使用ThreadLocal进行请求处理
 */
public class RequestHolder {

    //把用户包装进ThreadLocal进行处理
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

    //包装http请求
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

    //放入新的用户
    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);
    }

    //放入新的http请求
    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    //获取当前用户
    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    //获取当前请求
    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }

    //移除用户和请求
    public static void remove() {
        userHolder.remove();
        requestHolder.remove();
    }
}
