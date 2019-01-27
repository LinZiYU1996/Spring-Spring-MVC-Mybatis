package com.lin.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 构造部门层级的工具
 *
 * 根部门用 0 来表示
 *
 * 根部门下的子部门用 . 号分隔
 *
 * 如 0.1  0.1.2
 *
 */
public class LevelUtil {

    public final static String SEPARATOR = ".";//分隔符号

    public final static String ROOT = "0";//根部门

    // 0
    // 0.1
    // 0.1.2
    // 0.1.3
    //层级计算      给出上级部门的String形式和id
    public static String calculateLevel(String parentLevel, int parentId) {
        if (StringUtils.isBlank(parentLevel)) {//如果上级部门为空，说明是根部门 直接返回
            return ROOT;
        } else {
            return StringUtils.join(parentLevel, SEPARATOR, parentId);//否则用字符串工具 把两者用 . 号连接起来
        }
//        Integer
    }
}
