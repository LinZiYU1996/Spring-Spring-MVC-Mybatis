package com.lin.beans;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 封装分页数据
 */
public class PageQuery {

    @Getter
    @Setter
    @Min(value = 1, message = "当前页码不合法")
    private int pageNo = 1;//页码数

    @Getter
    @Setter
    @Min(value = 1, message = "每页展示数量不合法")
    private int pageSize = 10;//每页展示十条记录

    @Setter
    private int offset;//偏移量

    public int getOffset() {
        return (pageNo - 1) * pageSize;
    }
}
