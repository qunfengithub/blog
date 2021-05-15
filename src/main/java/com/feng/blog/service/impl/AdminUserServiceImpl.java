package com.feng.blog.service.impl;

import com.feng.blog.dao.AdminUserMapper;
import com.feng.blog.entity.AdminUser;
import com.feng.blog.service.AdminUserService;
import com.feng.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    //这时候就需要Mapper 去数据库查询数据了
    @Resource
    AdminUserMapper adminUserMapper;


    
    @Override
    public AdminUser login(String userName, String passWord) {
        String passwordMd5 = MD5Util.MD5Encode(passWord,"UTF-8");
        return adminUserMapper.login(userName,passwordMd5);
    }

    //查询登录的id
    @Override
    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    //在接口中实现该方法
    @Override
    public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
       //通过登录的id  获取到登录的登录的用户类
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //判断非空的
        if (adminUser!=null){
            //原始的密码和新密码进行MD5加密
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            //比较原始的密码是否正确   原始的密码相等的  才能修改新密码
            if (originalPasswordMd5.equals(adminUser.getLoginPassword())){
                //设置新的密码
                adminUser.setLoginPassword(newPasswordMd5);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                    //update  为 1 表示修改成功
                    return true;
                }
            }

        }

        return false;
    }


    //修改用户名的时候
    @Override
    public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {

        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        if (adminUser!=null){
            //修改信息  请输入原始名 再输入修改的名字
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            //修改用户
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                return true;
            }
            return false; //修改失败
        }

        return null;
    }
}
