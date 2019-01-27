package com.lin.common;

import com.lin.exception.ParamException;
import com.lin.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 全局异常的处理类
 */


@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    /*
    request--请求
    response--返回
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        String url = request.getRequestURL().toString();//把Url包装成String进行处理
        ModelAndView mv;//视图模型
        String defaultMsg = "System error";//默认信息（提示错误）

        // 这里要求项目中所有请求json数据，都使用.json结尾
        if (url.endsWith(".json")) {
            //如果是系统异常或参数异常
            if (ex instanceof PermissionException || ex instanceof ParamException) {
                JsonData result = JsonData.fail(ex.getMessage());//获取Json数据
                mv = new ModelAndView("jsonView", result.toMap());
            } else {
                //输出错误日志，报错Url及错误
                log.error("unknown json exception, url:" + url, ex);
                JsonData result = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView", result.toMap());
            }
        } else if (url.endsWith(".page")){ // 这里要求项目中所有请求page页面，都使用.page结尾
            log.error("unknown page exception, url:" + url, ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception", result.toMap());
        } else {
            log.error("unknow exception, url:" + url, ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView", result.toMap());
        }

        return mv;//返回视图模型
    }
}
