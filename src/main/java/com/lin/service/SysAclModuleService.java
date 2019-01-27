package com.lin.service;

import com.google.common.base.Preconditions;
import com.lin.common.RequestHolder;
import com.lin.dao.SysAclMapper;
import com.lin.dao.SysAclModuleMapper;
import com.lin.exception.ParamException;
import com.lin.model.SysAclModule;
import com.lin.param.AclModuleParam;
import com.lin.util.BeanValidator;
import com.lin.util.IpUtil;
import com.lin.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by linziyu on 2019/1/23.
 *
 * 权限模块Service
 */

@Service
public class SysAclModuleService {

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysLogService sysLogService;
//保存权限模块
    public void save(AclModuleParam param) {
        BeanValidator.check(param);//参数校验
        if(checkExist(param.getParentId(), param.getName(), param.getId())) {//是否重复
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();//通过建造者模式生成实例

        aclModule.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));//保存层级
        aclModule.setOperator(RequestHolder.getCurrentUser().getUsername());//保存操作者
        aclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));//保存操作IP
        aclModule.setOperateTime(new Date());//保存操作时间
        sysAclModuleMapper.insertSelective(aclModule);//插入Mysql
        sysLogService.saveAclModuleLog(null, aclModule);//日志记录
    }
//更新权限模块
    public void update(AclModuleParam param) {
        BeanValidator.check(param);//参数校验
        if(checkExist(param.getParentId(), param.getName(), param.getId())) {//是否重复
            throw new ParamException("同一层级下存在相同名称的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());//取出更新前权限模块
        Preconditions.checkNotNull(before, "待更新的权限模块不存在");//检查是否存在

        SysAclModule after = SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();//生成新的权限模块

        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());

        updateWithChild(before, after);//对MySql执行更新操作
        sysLogService.saveAclModuleLog(before, after);//日志记录
    }
//
    @Transactional
    private void updateWithChild(SysAclModule before, SysAclModule after) {
        String newLevelPrefix = after.getLevel();//新的层级
        String oldLevelPrefix = before.getLevel();//旧的层级
        if (!after.getLevel().equals(before.getLevel())) {//新 旧不同
            List<SysAclModule> aclModuleList = sysAclModuleMapper.getChildAclModuleListByLevel(before.getLevel());//根据旧的获取权限模块集合
            if (CollectionUtils.isNotEmpty(aclModuleList)) {//是否为空
                for (SysAclModule aclModule : aclModuleList) {//遍历
                    String level = aclModule.getLevel();//取出层级
                    if (level.indexOf(oldLevelPrefix) == 0) {
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        aclModule.setLevel(level);
                    }
                }
                sysAclModuleMapper.batchUpdateLevel(aclModuleList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKeySelective(after);//执行更新操作
    }

//检查是否重复
    private boolean checkExist(Integer parentId, String aclModuleName, Integer deptId) {
        return sysAclModuleMapper.countByNameAndParentId(parentId, aclModuleName, deptId) > 0;
    }
//获取权限模块层级
    private String getLevel(Integer aclModuleId) {
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);
        if (aclModule == null) {
            return null;
        }
        return aclModule.getLevel();
    }
//删除操作
    public void delete(int aclModuleId) {
        SysAclModule aclModule = sysAclModuleMapper.selectByPrimaryKey(aclModuleId);//取出模块
        Preconditions.checkNotNull(aclModule, "待删除的权限模块不存在，无法删除");//判断
        if(sysAclModuleMapper.countByParentId(aclModule.getId()) > 0) {
            throw new ParamException("当前模块下面有子模块，无法删除");
        }
        if (sysAclMapper.countByAclModuleId(aclModule.getId()) > 0) {
            throw new ParamException("当前模块下面有用户，无法删除");
        }
        sysAclModuleMapper.deleteByPrimaryKey(aclModuleId);//OK，执行删除
    }

}
