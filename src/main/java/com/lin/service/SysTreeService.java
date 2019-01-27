package com.lin.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.lin.dao.SysAclMapper;
import com.lin.dao.SysAclModuleMapper;
import com.lin.dao.SysDeptMapper;
import com.lin.dto.AclDto;
import com.lin.dto.AclModuleLevelDto;
import com.lin.dto.DeptLevelDto;
import com.lin.model.SysAcl;
import com.lin.model.SysAclModule;
import com.lin.model.SysDept;
import com.lin.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by linziyu on 2019/1/23.
 * 生成部门树 权限树Service
 *
 *
 */


@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysCoreService sysCoreService;
    @Resource
    private SysAclMapper sysAclMapper;


    //把用户权限转换成权限模块dto
    public List<AclModuleLevelDto> userAclTree(int userId) {
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);//根据用户id获取权限集合
        List<AclDto> aclDtoList = Lists.newArrayList();//存放权限dto的集合
        for (SysAcl acl : userAclList) {//遍历权限集合
            AclDto dto = AclDto.adapt(acl);//把权限类适配成dto
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId) {
        // 1、当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        // 2、当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        // 3、当前系统所有权限点
        List<AclDto> aclDtoList = Lists.newArrayList();

        //java 8 流式遍历 把用户的权限id放入set
        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        //java 8 流式遍历 把角色的权限id放入set
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll();//获取所有权限
        for (SysAcl acl : allAclList) {//遍历
            AclDto dto = AclDto.adapt(acl);//适配成权限dto
            if (userAclIdSet.contains(acl.getId())) {//如果当前用户的权限id和集合中的权限id一样
                dto.setHasAcl(true);//说明该用户有该权限
            }
            if (roleAclIdSet.contains(acl.getId())) {//如果当前角色的权限id和集合中的权限id一样
                dto.setChecked(true);//说明该角色有该权限
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {//空 直接返回空的集合 不作处理
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();
        //同样，一键多值  key放的是权限模块的id  value放到是该模块下的所有权限的集合
        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for(AclDto acl : aclDtoList) {//遍历
            if (acl.getStatus() == 1) {//该权限转态为1 说明该权限可用
                moduleIdAclMap.put(acl.getAclModuleId(), acl);//放入map
            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }


    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList, Multimap<Integer, AclDto> moduleIdAclMap) {
        if (CollectionUtils.isEmpty(aclModuleLevelList)) {//空值 直接返回 不做处理（也是递归的出口）
            return;
        }
        for (AclModuleLevelDto dto : aclModuleLevelList) {//遍历权限模块dto
            List<AclDto> aclDtoList = (List<AclDto>)moduleIdAclMap.get(dto.getId());//根据权限模块id取出对应的权限dto集合
            if (CollectionUtils.isNotEmpty(aclDtoList)) {//非空
                Collections.sort(aclDtoList, aclSeqComparator);//先把集合中的权限按照seq大小进行排序
                dto.setAclList(aclDtoList);//每个权限模块dto都维护了一个权限集合（就是这个权限模块下的所有权限的集合）
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);//递归操作
        }
    }




//返回权限模块dto
    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();//获取所有权限模块
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();//存放权限模块dto
        for (SysAclModule aclModule : aclModuleList) {//遍历
            dtoList.add(AclModuleLevelDto.adapt(aclModule));//适配
        }
        return aclModuleListToTree(dtoList);
    }
//把权限模块转换成层级树
    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        // level -> [aclmodule1, aclmodule2, ...] Map<String, List<Object>>
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        Collections.sort(rootList, aclModuleSeqComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }
//权限模块转换成层级树 核心操作
    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for (int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)) {
                Collections.sort(tempList, aclModuleSeqComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, levelAclModuleMap);
            }
        }
    }



//构造存放部门树层级适配类的集合
    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.getAllDept();//先获取所有部门数据 放入List中

        List<DeptLevelDto> dtoList = Lists.newArrayList();//创建一个存放部门适配类的List
        for (SysDept dept : deptList) {//进行遍历
            DeptLevelDto dto = DeptLevelDto.adapt(dept);//适配转换
            dtoList.add(dto);//放入List
        }
        return deptListToTree(dtoList);
    }


    //递归实现把部门转换成层级树
    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList) {
        if (CollectionUtils.isEmpty(deptLevelList)) {//空的话，直接返回空集合
            return Lists.newArrayList();
        }
        // level -> [dept1, dept2, ...] Map<String, List<Object>>

        //使用Multimap   key是当前dto的level  value是统一层级dto的结合
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();//存放一级部门

        for (DeptLevelDto dto : deptLevelList) {//遍历
            levelDeptMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {//如果当前是顶级部门  则加入
                rootList.add(dto);
            }
        }


        // 按照seq从小到大排序  重写自己的排序方法
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        // 递归生成树
        transformDeptTree(rootList, LevelUtil.ROOT, levelDeptMap);
        return rootList;
    }

    // level:0, 0, all 0->0.1,0.2
    // level:0.1
    // level:0.2
    //递归排序
    public void transformDeptTree(List<DeptLevelDto> deptLevelList, String level, Multimap<String, DeptLevelDto> levelDeptMap) {
        for (int i = 0; i < deptLevelList.size(); i++) {
            // 遍历该层的每个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);
            // 处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level, deptLevelDto.getId());
            // 处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptList)) {
                // 排序
                Collections.sort(tempDeptList, deptSeqComparator);
                // 设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                // 进入到下一层处理
                transformDeptTree(tempDeptList, nextLevel, levelDeptMap);
            }
        }
    }
//同一层级部门的排序方法 根据seq的大小来排序
    public Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
//同一层级权限模块的排序方法  根据seq的大小来排序
    public Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
//同一层级的权限的排序方法  根据seq的大小来排序
    public Comparator<AclDto> aclSeqComparator = new Comparator<AclDto>() {
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

}
