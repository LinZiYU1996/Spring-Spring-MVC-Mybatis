package com.lin.dto;

import com.google.common.collect.Lists;
import com.lin.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 权限模块适配
 */

@Getter
@Setter
@ToString
public class AclModuleLevelDto extends SysAclModule{


    private List<AclModuleLevelDto> aclModuleList = Lists.newArrayList();//维护一个权限模块集合

    private List<AclDto> aclList = Lists.newArrayList();//维护一个权限集合

    public static AclModuleLevelDto adapt(SysAclModule aclModule) {
        AclModuleLevelDto dto = new AclModuleLevelDto();
        BeanUtils.copyProperties(aclModule, dto);//把aclModule复制到dto
        return dto;
    }
}
