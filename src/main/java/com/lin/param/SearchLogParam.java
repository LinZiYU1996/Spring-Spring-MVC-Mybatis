package com.lin.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by linziyu on 2019/1/25.
 */

@Getter
@Setter
@ToString
public class SearchLogParam {

    private Integer type; // LogType

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private String fromTime;//yyyy-MM-dd HH:mm:ss

    private String toTime;
}
