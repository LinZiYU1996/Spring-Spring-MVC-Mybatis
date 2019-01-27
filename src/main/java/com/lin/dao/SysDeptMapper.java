package com.lin.dao;

import com.lin.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
部门操作接口
 */
public interface SysDeptMapper {
    int deleteByPrimaryKey(@Param("id") Integer id);//根据主键ID删除

    int insert(SysDept record);//插入

    int insertSelective(SysDept record);//查询

    SysDept selectByPrimaryKey(@Param("id")Integer id);//根据主键ID查询

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    //以下都是自定义的方法 需要到mapper文件夹下对应的xml文件中写对应的Sql语句才能实现 上面的是通过MyBatis Generator生成的，sql已经写好了

    List<SysDept> getAllDept();//获取所有部门数据

    List<SysDept> getChildDeptListByLevel(@Param("level") String level);

    void batchUpdateLevel(@Param("sysDeptList") List<SysDept> sysDeptList);

    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String name, @Param("id") Integer id);

    int countByParentId(@Param("deptId") int deptId);//根据部门ID统计部门个数
}