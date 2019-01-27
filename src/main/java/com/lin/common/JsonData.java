package com.lin.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linziyu on 2019/1/23.
 *
 *包装自己的Json数据（Json请求返回的结构）
 *
 *
 */

@Getter
@Setter
public class JsonData {

    private boolean ret;        //返回结果

    private String msg;     //有错误的时候返回的信息

    private Object data;        //正常的时候返回的结果

    public JsonData(boolean ret) {
        this.ret = ret;
    }

    /*
    成功的时候，返回信息
     */

    public static JsonData success(Object object, String msg) {
        JsonData jsonData = new JsonData(true);//结果设为True
        jsonData.data = object;
        jsonData.msg = msg;
        return jsonData;
    }
    /*
    成功的时候，不返回信息
     */
    public static JsonData success(Object object) {
        JsonData jsonData = new JsonData(true);//结果设为True
        jsonData.data = object;
        return jsonData;
    }

     /*
        成功的时候，不返回信息及Data
         */

    public static JsonData success() {
        return new JsonData(true);
    }



    /*
    失败的时候
     */
    public static JsonData fail(String msg) {
        JsonData jsonData = new JsonData(false);//结果设为False
        jsonData.msg = msg;
        return jsonData;
    }
    /*
    把Json数据封装到Map中
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("ret", ret);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }
}
