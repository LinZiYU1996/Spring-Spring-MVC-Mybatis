package com.lin.dto;

import com.lin.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 权限适配器
 */

@Getter
@Setter
@ToString
public class AclDto extends SysAcl{
    // 是否要默认选中
    private boolean checked = false;

    // 是否有权限操作
    private boolean hasAcl = false;

    public static AclDto adapt(SysAcl acl) {
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(acl, dto);//把acl对象复制给dto
        return dto;
    }
}
