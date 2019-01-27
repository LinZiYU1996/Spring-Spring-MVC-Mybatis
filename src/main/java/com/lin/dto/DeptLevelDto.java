package com.lin.dto;

import com.google.common.collect.Lists;
import com.lin.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by linziyu on 2019/1/23.
 * 部门层级结构的适配
 */


@Setter
@Getter
@ToString
public class DeptLevelDto extends SysDept{
    private List<DeptLevelDto> deptList = Lists.newArrayList();

    public static DeptLevelDto adapt(SysDept dept) {
        DeptLevelDto dto = new DeptLevelDto();
        BeanUtils.copyProperties(dept, dto);//把传递的对象拷贝到dto
        return dto;
    }

}
