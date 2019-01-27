package com.lin.controller;

import com.lin.dao.SysUserMapper;
import com.lin.model.SysUser;
import com.lin.service.SysUserService;
import com.lin.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by linziyu on 2019/1/24.
 *
 *
 * 处理用户登录和注销
 */

@Controller
public class UserController {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserService sysUserService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getSession().invalidate();
        String path = "signin.jsp";//登录jsp
        response.sendRedirect(path);//重新返回到登录界面
    }

    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


//        String password1 = "123";
//        //构造用户
//        SysUser user = SysUser.builder().username("adm").telephone("4544545").mail("ass@1.com")
//                .password(password1).deptId(0).status(1).remark("").build();
//        user.setOperator("");
//        user.setOperateIp("");
//        user.setOperateTime(new Date());
//        sysUserMapper.insertSelective(user);//插入数据
//
        String username = request.getParameter("username");//获取用户名字
        String password = request.getParameter("password");//获取密码


        SysUser sysUser = sysUserService.findByKeyword(username);//通过用户名进行查找
        String errorMsg = "";//定义返回的信息
        String ret = request.getParameter("ret");//从请求中获取 ret 参数的值

        if (StringUtils.isBlank(username)) {//对用户名字进行判空
            errorMsg = "用户名不可以为空";
        } else if (StringUtils.isBlank(password)) {//对用户密码进行判空
            errorMsg = "密码不可以为空";
        }
        else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))) {
                errorMsg = "用户名或密码错误";
        } else if (sysUser == null) {//查找返回是否为空
            errorMsg = "查询不到指定的用户";

        } else if (sysUser.getStatus() != 1) {//用户是否状态被冻结
            errorMsg = "用户已被冻结，请联系管理员";
        } else {
            // login success
            request.getSession().setAttribute("user", sysUser);//以上都OK 把用户放进参数user 中
            if (StringUtils.isNotBlank(ret)) {//如果返回信息非空
                response.sendRedirect(ret);
            } else {
                response.sendRedirect("/admin/index.page"); //TODO
            }
        }

        request.setAttribute("error", errorMsg);//传入错误信息
        request.setAttribute("username", username);//传入用户名
        if (StringUtils.isNotBlank(ret)) {
            request.setAttribute("ret", ret);
        }
        String path = "signin.jsp";
        request.getRequestDispatcher(path).forward(request, response);//页面重定向
    }
}
