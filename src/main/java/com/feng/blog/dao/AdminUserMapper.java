package com.feng.blog.dao;

import com.feng.blog.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

//使用的是Mybatis
public interface AdminUserMapper {


    /**
     * 登录的方法 查询数据库中有没有
     * @param userName
     * @param passWord
     * @return
     */

    //登录的功能  查询的
    AdminUser login(@Param("userName") String userName, @Param("password") String passWord);

    //通过adminUserId 去查询到登录的实体类
    AdminUser selectByPrimaryKey(Integer adminUserId);

    int updateByPrimaryKeySelective(AdminUser record);

    int updateByPrimaryKey(AdminUser record);
}
