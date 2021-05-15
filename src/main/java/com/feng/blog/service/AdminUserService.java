package com.feng.blog.service;

import com.feng.blog.entity.AdminUser;

public interface AdminUserService {

    AdminUser login(String userName, String passWord);

    AdminUser getUserDetailById(Integer loginUserId);

    Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);

    Boolean updateName(Integer loginUserId, String loginUserName, String nickName);
}
