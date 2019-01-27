package com.lin.dao;

import com.lin.model.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
角色权限的Sql操作
 */
public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);
    //以下都是自定义的方法 需要到mapper文件夹下对应的xml文件中写对应的Sql语句才能实现 上面的是通过MyBatis Generator生成的，sql已经写好了
    List<Integer> getAclIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    void deleteByRoleId(@Param("roleId") int roleId);

    void batchInsert(@Param("roleAclList") List<SysRoleAcl> roleAclList);

    List<Integer> getRoleIdListByAclId(@Param("aclId") int aclId);
}