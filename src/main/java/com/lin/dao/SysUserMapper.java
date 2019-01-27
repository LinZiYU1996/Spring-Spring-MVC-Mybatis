package com.lin.dao;

import com.lin.beans.PageQuery;
import com.lin.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/*
用户的Sql操作
 */
public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);
    //以下都是自定义的方法 需要到mapper文件夹下对应的xml文件中写对应的Sql语句才能实现 上面的是通过MyBatis Generator生成的，sql已经写好了
    SysUser findByKeyword(@Param("keyword") String keyword);

    int countByMail(@Param("mail") String mail, @Param("id") Integer id);

    int countByTelephone(@Param("telephone") String telephone, @Param("id") Integer id);

    int countByDeptId(@Param("deptId") int deptId);

    List<SysUser> getPageByDeptId(@Param("deptId") int deptId, @Param("page") PageQuery page);

    List<SysUser> getByIdList(@Param("idList") List<Integer> idList);

    List<SysUser> getAll();
}