package com.feng.blog.interceptor;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后台系统身份的验证的拦截器
 *
 *
 * 未登录后 拦截页面
 */

@Component
public class AdminLoginInterceptor  implements HandlerInterceptor {

    //在业务处理器处理请求之前 被调用 。预处理  ，可以进行编码，安全控制  权限校验等功能
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取程序输入url 的路径
        String uri = request.getServletPath();

        //开始的时 admin /后面的路径 要拦截 防止 出现的直接进入后面的路径的情况
        if (uri.startsWith("/admin") && null ==request.getSession().getAttribute("loginUser")){

            //在Session 中 设置 错误的信息 ，可以重新登录的
            request.getSession().setAttribute("errorMag","请登录");
            
            //响应到重定向的 登录的页面去
            response.sendRedirect(request.getContextPath()+"/admin/login");

            //返回错误
            return false;

        }else{
            //登录到页面  移除错误的信息
            request.getSession().removeAttribute("errorMsg");
        }


        return false;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
