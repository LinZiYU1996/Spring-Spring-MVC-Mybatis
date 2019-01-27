package com.lin.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.lin.common.RequestHolder;
import com.lin.dao.SysRoleAclMapper;
import com.lin.dao.SysRoleMapper;
import com.lin.dao.SysRoleUserMapper;
import com.lin.dao.SysUserMapper;
import com.lin.exception.ParamException;
import com.lin.model.SysRole;
import com.lin.model.SysUser;
import com.lin.param.RoleParam;
import com.lin.util.BeanValidator;
import com.lin.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by linziyu on 2019/1/24.
 *
 * 角色Service
 */

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleUserMapper sysRoleUserMapper;
    @Resource
    private SysRoleAclMapper sysRoleAclMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysLogService sysLogService;
//保存操作
    public void save(RoleParam param) {
        BeanValidator.check(param);//参数校验
        if (checkExist(param.getName(), param.getId())) {//是否已经存在
            throw new ParamException("角色名称已经存在");
        }
        SysRole role = SysRole.builder().name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();//生成角色
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        sysRoleMapper.insertSelective(role);//插入数据
        sysLogService.saveRoleLog(null, role);//日志记录
    }
//更新操作
    public void update(RoleParam param) {
        BeanValidator.check(param);//参数校验
        if (checkExist(param.getName(), param.getId())) {//是否已经存在
            throw new ParamException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(param.getId());//取出先前角色
        Preconditions.checkNotNull(before, "待更新的角色不存在");//检查是否为空

        SysRole after = SysRole.builder().id(param.getId()).name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();//生成新的角色
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(after);//更新操作
        sysLogService.saveRoleLog(before, after);//日志记录
    }
//获取所有角色
    public List<SysRole> getAll() {
        return sysRoleMapper.getAll();
    }
//检查是否存在
    private boolean checkExist(String name, Integer id) {
        return sysRoleMapper.countByName(name, id) > 0;
    }
//根据用户ID来获取角色
    public List<SysRole> getRoleListByUserId(int userId) {
        List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)) {
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }
//根据权限ID获取角色
    public List<SysRole> getRoleListByAclId(int aclId) {
        List<Integer> roleIdList = sysRoleAclMapper.getRoleIdListByAclId(aclId);
        if (CollectionUtils.isEmpty(roleIdList)) {//判断是否为空
            return Lists.newArrayList();
        }
        return sysRoleMapper.getByIdList(roleIdList);
    }
//根据角色集合来获取对应的用户集合
    public List<SysUser> getUserListByRoleList(List<SysRole> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {//判断是否为空
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = roleList.stream().map(role -> role.getId()).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return sysUserMapper.getByIdList(userIdList);
    }
}
