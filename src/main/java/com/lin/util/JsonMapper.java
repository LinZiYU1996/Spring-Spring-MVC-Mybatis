package com.lin.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

/**
 * Created by linziyu on 2019/1/22.
 *
 * json转换工具
 */

@Slf4j
public class JsonMapper {

    private static ObjectMapper objectMapper = new ObjectMapper();


    /*
    类加载的时候执行，进行初始化配置
     */
    static {
        // config
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    /*
    对象转字符串
     */
    public static <T> String obj2String(T src) {
        if (src == null) {
            return null;//空的话，就直接返回空值
        }
        try {
            //进行比较，看当前对象是否是字符串，是就以字符串形式返回
            //不然就调用 objectMapper.writeValueAsString转换成String
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        } catch (Exception e) {
            //失败的话就输出错误日志，并且返回空值
            log.warn("parse object to String exception, error:{}", e);
            return null;
        }
    }
    /*
    String 转对象
    TypeReference---类型引用
     */
    public static <T> T string2Obj(String src, TypeReference<T> typeReference) {
        if (src == null || typeReference == null) {
            return null;//空的话，就直接返回空值
        }
        try {
            //TypeReference是String的话，直接返回
            //不然接
            return (T) (typeReference.getType().equals(String.class) ? src : objectMapper.readValue(src, typeReference));
        } catch (Exception e) {
            log.warn("parse String to Object exception, String:{}, TypeReference<T>:{}, error:{}", src, typeReference.getType(), e);
            return null;
        }
    }
}
