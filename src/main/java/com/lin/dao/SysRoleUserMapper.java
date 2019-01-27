package com.lin.dao;

import com.lin.model.SysRoleUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/*
用户角色的Sql操作
 */
public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);
    //以下都是自定义的方法 需要到mapper文件夹下对应的xml文件中写对应的Sql语句才能实现 上面的是通过MyBatis Generator生成的，sql已经写好了
    List<Integer> getRoleIdListByUserId(@Param("userId") int userId);

    List<Integer> getUserIdListByRoleId(@Param("roleId") int roleId);

    void deleteByRoleId(@Param("roleId") int roleId);

    void batchInsert(@Param("roleUserList") List<SysRoleUser> roleUserList);

    List<Integer> getUserIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);
}