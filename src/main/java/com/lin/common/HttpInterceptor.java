package com.lin.common;

import com.lin.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by linziyu on 2019/1/23.
 *
 *
 * Http请求前后的监听
 *
 *
 * 继承HandlerInterceptorAdapter用于简化仅前/仅后拦截器的实现
 */


@Slf4j
public class HttpInterceptor extends  HandlerInterceptorAdapter {
    private static final String START_TIME = "requestStartTime";    //全局变量，请求开始时间


    /*
    参数:

    请求——当前HTTP请求

    响应-当前HTTP响应

    为类型和/或实例计算选择要执行的处理程序

    返回:

    如果执行链应该继续处理下一个拦截器或处理程序本身，则为。否则，DispatcherServlet假设这个拦截器已经处理了响应本身。

    抛出:

. lang。例外-在错误的情况下
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI().toString();        //获取URL
        Map parameterMap = request.getParameterMap();       //获取参数
        log.info("request start. url:{}, params:{}", url, JsonMapper.obj2String(parameterMap));//输出日志
        long start = System.currentTimeMillis();//获取请求开始的时间
        request.setAttribute(START_TIME, start);//把时间赋值给参数
        return true;
    }


    /*
    后置接口处理拦截器

参数:

请求——当前HTTP请求

响应-当前HTTP响应

对于类型和/或实例检查，启动异步执行的处理程序(或HandlerMethod)

modelAndView——处理程序返回的modelAndView(也可以是null)

抛出:

. lang。例外-在错误的情况下
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String url = request.getRequestURI().toString();
//        long start = (Long) request.getAttribute(START_TIME);
//        long end = System.currentTimeMillis();
//        log.info("request finished. url:{}, cost:{}", url, end - start);
        removeThreadLocalInfo();
    }


    /*
    接口处理拦截器完成后

参数:

请求——当前HTTP请求

响应-当前HTTP响应

对于类型和/或实例检查，启动异步执行的处理程序(或HandlerMethod)

在处理程序执行时抛出异常(如果有的话)

抛出:

. lang。例外-在错误的情况下
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url = request.getRequestURI().toString();//获取Url
        long start = (Long) request.getAttribute(START_TIME);//获取开始时间
        long end = System.currentTimeMillis();//得到结束时间
        log.info("request completed. url:{}, cost:{}", url, end - start);//日志输出时间花费

        removeThreadLocalInfo();
    }

    public void removeThreadLocalInfo() {
//        RequestHolder.remove();;
    }



}
