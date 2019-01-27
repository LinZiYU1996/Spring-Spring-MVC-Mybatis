package com.lin.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.lin.common.ApplicationContextHelper;
import com.lin.common.JsonData;
import com.lin.common.RequestHolder;
import com.lin.model.SysUser;
import com.lin.service.SysCoreService;
import com.lin.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by linziyu on 2019/1/25.
 *
 *
 * 权限过滤器
 */

@Slf4j
public class AclControlFilter implements Filter{
    private static Set<String> exclusionUrlSet = Sets.newConcurrentHashSet();//存放的是不会被进行过滤的请求

    private final static String noAuthUrl = "/sys/user/noAuth.page";//当无权限访问时跳转的url


//初始化操作
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");//从配置文件web.xml获取
        List<String> exclusionUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);//对逗号进行分隔，并去除空格

        exclusionUrlSet = Sets.newConcurrentHashSet(exclusionUrlList);//把不需要过滤的Url放入Set里面
        exclusionUrlSet.add(noAuthUrl);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String servletPath = request.getServletPath();//取出请求路径
        Map requestMap = request.getParameterMap();

        if (exclusionUrlSet.contains(servletPath)) {//如果当前请求属于无需过滤的集合中，那就直接执行下一步
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        SysUser sysUser = RequestHolder.getCurrentUser();//取出用户
        if (sysUser == null) {//用户为空的话  说明该用户没有登录
            log.info("someone visit {}, but no login, parameter:{}", servletPath, JsonMapper.obj2String(requestMap));//输出日志
            noAuth(request, response);//无权限处理
            return;
        }
        SysCoreService sysCoreService = ApplicationContextHelper.popBean(SysCoreService.class);//取出上下文中的SysCoreService的Bean
        if (!sysCoreService.hasUrlAcl(servletPath)) {//当前用户没有访问权限
            log.info("{} visit {}, but no login, parameter:{}", JsonMapper.obj2String(sysUser), servletPath, JsonMapper.obj2String(requestMap));//输出日志
            noAuth(request, response);//无权限处理
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
        return;
    }
//处理无权限时的情况
    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String servletPath = request.getServletPath();
        if (servletPath.endsWith(".json")) {//如果当前是Json请求
            JsonData jsonData = JsonData.fail("没有访问权限，如需要访问，请联系管理员");//给定错误信息
            response.setHeader("Content-Type", "application/json");
            response.getWriter().print(JsonMapper.obj2String(jsonData));
            return;
        } else {
            clientRedirect(noAuthUrl, response);
            return;
        }
    }
//页面定向跳转
    private void clientRedirect(String url, HttpServletResponse response) throws IOException{
        response.setHeader("Content-Type", "text/html");
        response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
    }



    @Override
    public void destroy() {

    }
}
