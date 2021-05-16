package com.feng.blog.controller.admin;


import com.feng.blog.entity.AdminUser;
import com.feng.blog.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {


    @Resource
    AdminUserService adminUserService;



    //登录的页面
    @GetMapping("/login")
    public String login(){

        //重定向到页面
        return "admin/login";
    }

    //可能有多个路径的情况
    @GetMapping({"","/","index","index.html"})
    public String index(HttpServletRequest request){
        request.setAttribute("path","index");
      //  request.setAttribute("categoryCount",);


        return "admin/index";
    }


    //登录的接口     登录的流程 就是 先 验证用户名 账号 验证码 是否为null 然后创建一个值
    @PostMapping(value = "/login")
    public String login(@RequestParam("UserName") String userName,
                        @RequestParam("Password") String passWord,
                        @RequestParam("verifyCode")String verifyCode,
                        HttpSession session){

        //验证参数
        if (StringUtils.isEmpty(verifyCode)){
            //把错误信息存储到Session
            session.setAttribute("errorMsg","验证码不正确");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)){
            session.setAttribute("errorMsg","用户名和密码不能为空");
            return "admin/login";
        }
        //Session 获取验证码
        String kaptchaCode = session.getAttribute("verifyCode") + " ";
        if (StringUtils.isEmpty(kaptchaCode) || kaptchaCode !=verifyCode){
            session.setAttribute("errorMsg","验证码错误");
            return "admin/login";
        }
        //从数据库中查询到用户名和密码
        AdminUser adminUser = adminUserService.login(userName, passWord);
        if (adminUser!=null){
            session.setAttribute("loginUser",adminUser.getNickName());// 把用户名存储到Session中
            session.setAttribute("loginUserId",adminUser.getAdminUserId());//
            //Session设置过期的时间
            session.setMaxInactiveInterval(60*60*2);
            return "redirect:/admin/index";//重定向到首页卖弄
        }else {
            //Session中设置值
            session.setAttribute("errorMsg","用户名和密码不正确");
            return "admin/login"; //跳转到登录的页面
        }


    }






    //登录后的轮廓的接口的
    @GetMapping("/profile")
    public String profile (HttpServletRequest request){

        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        //通过 Id 查询登录的信息
        AdminUser adminUser =adminUserService.getUserDetailById(loginUserId);
        if (adminUser ==null){
            //返回登录的页面
            return "admin/login";
        }
        request.setAttribute("path","profile");
        request.setAttribute("loginUserName",adminUser);
        //相当于的外号的意思
        request.setAttribute("nickName",adminUser.getNickName());

        return "admin/profile";
    }


    //修改密码的接口
    @PostMapping("/profile/password")
    @ResponseBody  //以json的格式去提交的情况
    public String passwordUpdate(HttpServletRequest request,@RequestParam("originalPassword") String  originalPassword,
     @RequestParam("newPassword")String newPassword){
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)){
            return "原始密码和新密码不能为空";
        }

        //获取到登录的UserId
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        //先写出需要的参数  然后进行服务中添加方法
        if (adminUserService.updatePassword(loginUserId,originalPassword,newPassword)){
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return "修改成功";
        }else{
            return "修改失败";
        }

    }

    //修改用户名 的接口 nickNam原来的用户名
    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request,@RequestParam ("loginUserName")String loginUserName,
                          @RequestParam ("nickName")String nickName){

        //先是判断用户名和参数不能为null   然后通过session 获取到登录的id ,去查询登录的用户名
        if (loginUserName==null || nickName ==null){
            return "用户名的参数不能为null";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId,loginUserName,nickName)){
           return "修改用户名成功";
        }else {
            return "修改失败";
        }

    }

    //退出系统登录 退出后最后主页面   退出系统需要移除Session的值
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUserName");
        request.getSession().removeAttribute("errorMsg");

        return "admin/login";//返回到登录的页面
    }




}
