package com.lin.filter;

import com.lin.common.RequestHolder;
import com.lin.model.SysUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 登录拦截
 */
public class LoginFilter implements Filter{


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;//http请求
        HttpServletResponse resp = (HttpServletResponse) servletResponse;//http返回

        SysUser sysUser = (SysUser)req.getSession().getAttribute("user");//从session取用户
        if (sysUser == null) {//为空 说明用户没有登录    直接返回到登录界面
            String path = "/signin.jsp";
            resp.sendRedirect(path);
            return;
        }
        RequestHolder.add(sysUser);//在全局中加入用户
        RequestHolder.add(req);//加入请求
        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }

    @Override
    public void destroy() {

    }
}
