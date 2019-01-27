package com.lin.dao;

import com.lin.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;



/*
角色的Sql操作
 */
public interface SysRoleMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);
    //以下都是自定义的方法 需要到mapper文件夹下对应的xml文件中写对应的Sql语句才能实现 上面的是通过MyBatis Generator生成的，sql已经写好了
    List<SysRole> getAll();

    int countByName(@Param("name") String name, @Param("id") Integer id);

    List<SysRole> getByIdList(@Param("idList") List<Integer> idList);


}