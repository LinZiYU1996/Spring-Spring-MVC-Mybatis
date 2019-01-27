package com.lin.service;

import com.google.common.base.Preconditions;
import com.lin.beans.PageQuery;
import com.lin.beans.PageResult;
import com.lin.common.RequestHolder;
import com.lin.dao.SysAclMapper;
import com.lin.exception.ParamException;
import com.lin.model.SysAcl;
import com.lin.param.AclParam;
import com.lin.util.BeanValidator;
import com.lin.util.IpUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by linziyu on 2019/1/24.
 *
 * 权限Service
 */

@Service
public class SysAclService {

    @Resource
    private SysAclMapper sysAclMapper;
    @Resource
    private SysLogService sysLogService;
//保存
    public void save(AclParam param) {
        BeanValidator.check(param);//参数校验
        if (checkExist(param.getAclModuleId(), param.getName(), param.getId())) {//是否重复
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();//生成新对象
        acl.setCode(generateCode());
        acl.setOperator(RequestHolder.getCurrentUser().getUsername());
        acl.setOperateTime(new Date());
        acl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAclMapper.insertSelective(acl);//执行插入操作
        sysLogService.saveAclLog(null, acl);//日志记录
    }
//更新
    public void update(AclParam param) {
        BeanValidator.check(param);//参数校验
        if (checkExist(param.getAclModuleId(), param.getName(), param.getId())) {//是否重复
            throw new ParamException("当前权限模块下面存在相同名称的权限点");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(param.getId());//更新前权限
        Preconditions.checkNotNull(before, "待更新的权限点不存在");//是否存在

        SysAcl after = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();//生成权限
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));

        sysAclMapper.updateByPrimaryKeySelective(after);//进行更新操作
        sysLogService.saveAclLog(before, after);//日志记录
    }
//检查是否存在
    public boolean checkExist(int aclModuleId, String name, Integer id) {
        return sysAclMapper.countByNameAndAclModuleId(aclModuleId, name, id) > 0;
    }
//生成格式化日期
    public String generateCode() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()) + "_" + (int)(Math.random() * 100);
    }
//返回权限集合（用于分页操作）
    public PageResult<SysAcl> getPageByAclModuleId(int aclModuleId, PageQuery page) {
        BeanValidator.check(page);
        int count = sysAclMapper.countByAclModuleId(aclModuleId);
        if (count > 0) {
            List<SysAcl> aclList = sysAclMapper.getPageByAclModuleId(aclModuleId, page);
            return PageResult.<SysAcl>builder().data(aclList).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }


}
