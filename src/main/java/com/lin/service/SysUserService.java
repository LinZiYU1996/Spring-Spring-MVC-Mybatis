package com.lin.service;

import com.google.common.base.Preconditions;
import com.lin.beans.PageQuery;
import com.lin.beans.PageResult;
import com.lin.common.RequestHolder;
import com.lin.dao.SysUserMapper;
import com.lin.exception.ParamException;
import com.lin.model.SysUser;
import com.lin.param.UserParam;
import com.lin.util.BeanValidator;
import com.lin.util.IpUtil;
import com.lin.util.MD5Util;
import com.lin.util.PasswordUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by linziyu on 2019/1/23.
 *用户Service
 *
 */

@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private  SysLogService sysLogService;

    //保存用户
    public void save(UserParam param) {
        BeanValidator.check(param);//参数校验
        //判断电话和邮箱是否已经存在
        if(checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已被占用");
        }

        //生成随机密码
        String password = PasswordUtil.randomPassword();
        //TODO:
        password = "123";//此处为了方便，就直接设置所有用户密码都为123
        //进行MD5加密
        String encryptedPassword = MD5Util.encrypt(password);

        //构造用户
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());

        // TODO: sendEmail 没有进行发送邮箱的实现

        sysUserMapper.insertSelective(user);//插入数据

        sysLogService.saveUserLog(null, user);//日志记录
    }
    //更新用户操作
    public void update(UserParam param) {

        BeanValidator.check(param);//参数校验

        //判断电话和邮箱是否已经存在
        if(checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException("电话已被占用");
        }
        if(checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已被占用");
        }

        //取出更改前的用户
        SysUser before = sysUserMapper.selectByPrimaryKey(param.getId());

        //判断是否为空
        Preconditions.checkNotNull(before, "待更新的用户不存在");

        //构造新的用户
        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());

        sysUserMapper.updateByPrimaryKeySelective(after);//执行更新操作

        sysLogService.saveUserLog(before, after);//日志记录
    }
//检查邮箱是否已经存在
    public boolean checkEmailExist(String mail, Integer userId) {
        return sysUserMapper.countByMail(mail, userId) > 0;
    }
//检查电话号码是否已经存在
    public boolean checkTelephoneExist(String telephone, Integer userId) {
        return sysUserMapper.countByTelephone(telephone, userId) > 0;
    }
//根据关键字查询用户
    public SysUser findByKeyword(String keyword) {
        return sysUserMapper.findByKeyword(keyword);
    }
//返回用户集合（用于分页操作）
    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page) {
        BeanValidator.check(page);
        int count = sysUserMapper.countByDeptId(deptId);//计算给定部门下有多少用户
        if (count > 0) {//有用户的话
            List<SysUser> list = sysUserMapper.getPageByDeptId(deptId, page);
            return PageResult.<SysUser>builder().total(count).data(list).build();
        }
        return PageResult.<SysUser>builder().build();
    }
//获取所有用户
    public List<SysUser> getAll() {
        return sysUserMapper.getAll();
    }


}
