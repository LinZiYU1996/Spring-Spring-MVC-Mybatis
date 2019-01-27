package com.lin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by linziyu on 2019/1/22.
 */

@Controller
@RequestMapping("/t1")
@Slf4j
public class TestController {

    @RequestMapping("/h1")
    @ResponseBody
    public String hello() {
//        log.info("hello");
//        throw new PermissionException("test exception");
        // return JsonData.success("hello, permission");
        return "hello";
    }


}
