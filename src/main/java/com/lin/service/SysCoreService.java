package com.lin.service;

import com.google.common.collect.Lists;
import com.lin.beans.CacheKeyConstants;
import com.lin.common.RequestHolder;
import com.lin.dao.SysAclMapper;
import com.lin.dao.SysRoleAclMapper;
import com.lin.dao.SysRoleUserMapper;
import com.lin.model.SysAcl;
import com.lin.model.SysUser;
import com.lin.util.JsonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 核心Service
 */

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper sysAclMapper;


    @Resource
    private SysRoleUserMapper sysRoleUserMapper;


    @Resource
    private SysRoleAclMapper sysRoleAclMapper;

    @Resource
    private SysCacheService sysCacheService;

//获取当前用户的权限集合
    public List<SysAcl> getCurrentUserAclList() {
        int userId = RequestHolder.getCurrentUser().getId();//取用户id
        return getUserAclList(userId);//根据id值取出对应的权限
    }
//获取角色权限集合
    public List<SysAcl> getRoleAclList(int roleId) {
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.<Integer>newArrayList(roleId));//根据角色id获取权限id集合
        if (CollectionUtils.isEmpty(aclIdList)) {//空 直接返回空的集合
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(aclIdList);//权限id获取权限集合
    }
//获取用户权限集合
    public List<SysAcl> getUserAclList(int userId) {
        if (isSuperAdmin()) {//是否为超级管理员
            return sysAclMapper.getAll();//是 就直接返回所有权限
        }
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(userId);//根据用户ID取出用户角色id
        if (CollectionUtils.isEmpty(userRoleIdList)) {//空的话直接返回空集合
            return Lists.newArrayList();
        }
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);//根据上面的角色id来取出相应的权限id
        if (CollectionUtils.isEmpty(userAclIdList)) {//空的话直接返回空集合
            return Lists.newArrayList();
        }
        return sysAclMapper.getByIdList(userAclIdList);//根据权限id查找权限，返回集合
    }
//是否为超级管理员
    public boolean isSuperAdmin() {
        // 这里是我自己定义了一个假的超级管理员规则，实际中要根据项目进行修改
        // 可以是配置文件获取，可以指定某个用户，也可以指定某个角色
        SysUser sysUser = RequestHolder.getCurrentUser();//获取当前用户
        if (sysUser.getMail().contains("admin")) {
            return true;
        }
        return false;
    }

//判断是否有权限访问该URL
    public boolean hasUrlAcl(String url) {
        if (isSuperAdmin()) {//是超级管理员的话 直接返回True
            return true;
        }
        List<SysAcl> aclList = sysAclMapper.getByUrl(url);//根据url来获取相应的权限集合
        if (CollectionUtils.isEmpty(aclList)) {//空的话，说明该url都不在权限管理范围下，也就是不需要权限许可谁都可以进入
            return true;
        }

        List<SysAcl> userAclList = getCurrentUserAclListFromCache();//从缓存中取出当前用户权限
        Set<Integer> userAclIdSet = userAclList.stream().map(acl -> acl.getId()).collect(Collectors.toSet());//Java8的流 获取用户权限id

        boolean hasValidAcl = false;//标记，是否有合规的权限点
        // 规则：只要有一个权限点有权限，那么我们就认为有访问权限
        for (SysAcl acl : aclList) {//遍历
            // 判断一个用户是否具有某个权限点的访问权限
            if (acl == null || acl.getStatus() != 1) { // 权限点无效 也就是该权限点已经作废了 不需要进行权限校验
                continue;
            }
            hasValidAcl = true;//有合规权限点
            if (userAclIdSet.contains(acl.getId())) {//用户权限ID与当前权限ID是否一样（即判断用户是否有该权限）
                return true;
            }
        }
        if (!hasValidAcl) {
            return true;
        }
        return false;
    }
//从缓存中取出当前用户权限
    public List<SysAcl> getCurrentUserAclListFromCache() {
        int userId = RequestHolder.getCurrentUser().getId();//获取当前用户ID
        String cacheValue = sysCacheService.getFromCache(CacheKeyConstants.USER_ACLS, String.valueOf(userId));
        if (StringUtils.isBlank(cacheValue)) {
            List<SysAcl> aclList = getCurrentUserAclList();
            if (CollectionUtils.isNotEmpty(aclList)) {
                sysCacheService.saveCache(JsonMapper.obj2String(aclList), 600, CacheKeyConstants.USER_ACLS, String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.string2Obj(cacheValue, new TypeReference<List<SysAcl>>() {
        });
    }
}
