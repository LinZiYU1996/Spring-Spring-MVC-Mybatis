MyBatis Generator是根据数据库自动生成Model Dao 及映射Xml文件的方便工具

![enter description here](./images/M1.PNG "M1")
下载好后放入项目下

工具包含的内容：
![enter description here](./images/M2_1.PNG "M2")





关键：进行xml文件配置：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN" "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
	<!-- 数据库驱动包位置 -->
	<classPathEntry location="D:\IDEA_WORk\Project_Managerment\generator\mysql-connector-java-5.1.34.jar" /> <!-- 1 -->备注：此处应该是你的jar包的路径
	<context id="DB2Tables" targetRuntime="MyBatis3">
		<commentGenerator>
			<property name="suppressAllComments" value="true" />
		</commentGenerator>
		<!-- 数据库链接URL、用户名、密码 -->
		<jdbcConnection driverClass="com.mysql.jdbc.Driver" connectionURL="jdbc:mysql://localhost:3306/manage?characterEncoding=utf8" userId="****" password="********">  <!-- 2 -->备注：你的Mysql用户名和密码
		</jdbcConnection>
		<javaTypeResolver>
			<property name="forceBigDecimals" value="false" />
		</javaTypeResolver>
		<!-- 生成模型的包名和位置 --> <!-- 3 -->
		<javaModelGenerator targetPackage="com.lin.model" targetProject="D:\IDEA_WORk\Project_Managerment\generator\src">备注：放置生成文件位置
			<property name="enableSubPackages" value="true" />
			<property name="trimStrings" value="true" />
		</javaModelGenerator>
		<!-- 生成的映射文件包名和位置 --> <!-- 4 -->备注：放置生成文件位置
		<sqlMapGenerator targetPackage="com.lin.mapper" targetProject="D:\IDEA_WORk\Project_Managerment\generator\src">
			<property name="enableSubPackages" value="true" />
		</sqlMapGenerator>
		<!-- 生成DAO的包名和位置 --> <!-- 5 -->备注：放置生成文件位置
		<javaClientGenerator type="XMLMAPPER" targetPackage="com.lin.dao" targetProject="D:\IDEA_WORk\Project_Managerment\generator\src">
			<property name="enableSubPackages" value="true" />
		</javaClientGenerator>
		<!-- 要生成那些表(更改tableName和domainObjectName就可以) --><!-- 6 -->备注：放置生成文件位置
		<table tableName="sys_user" domainObjectName="SysUser" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />// 备注：举例说明，下面的一样，只需关注tableName
	//tableName应该对应你的mysql数据库中对应的字段名字
		
		<table tableName="sys_dept" domainObjectName="SysDept" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
		<table tableName="sys_acl" domainObjectName="SysAcl" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
		<table tableName="sys_acl_module" domainObjectName="SysAclModule" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
		<table tableName="sys_role" domainObjectName="SysRole" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
		<table tableName="sys_role_acl" domainObjectName="SysRoleAcl" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
		<table tableName="sys_role_user" domainObjectName="SysRoleUser" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
		<table tableName="sys_log" domainObjectName="SysLog" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false" />
	</context>
</generatorConfiguration>

```
配置好Xml文件后，进入IDEA的控制台

![enter description here](./images/M3.PNG "M3")

控制台进入 Generator目录下执行语句

##### java -jar mybatis-generator-core-1.3.2.jar -configfile generatorConfig.xml -overwrite

 上面的mybatis-generator-core-1.3.2.jar 版本根据你下载的进行更改
 
 生成文件后拷贝到自己的Model Dao 和
  Mapper下