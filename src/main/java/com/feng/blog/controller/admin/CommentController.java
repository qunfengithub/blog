package com.feng.blog.controller.admin;

import com.feng.blog.service.CommentService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.Result;
import com.feng.blog.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CommentController {


    @Autowired
    CommentService commentService;

    @GetMapping("/comments")
    public String list(HttpServletRequest request){
        request.setAttribute("path","comment");
        return "admin/comment";
    }

    /**
     * 1.评论列表分页接口
     * 2.评论审核接口
     * 3.评论回复接口
     * 4.删除评论接口
     */


    /**
     * 评论列表
     *
     */
    @GetMapping("/comments/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){

        //验证参数
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！");
        }

        //分页获取到参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(commentService.getCommentPage(pageUtil));
    }

    /**
     * 评论审核
     */

    @PostMapping("/comments/checkDone")
    @ResponseBody
    public Result checkDone(@RequestBody Integer [] ids){ //请求的参数
        //验证参数的 是否
        if (ids.length<1){
            return ResultGenerator.genFailResult("参数异常！");
        }

        //审核
        if (commentService.checkDone(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("审核失败");
        }

    }


    /**
     * 评论回复接口
     */
    @GetMapping("/comment/reply")
    @ResponseBody
    public Result checkDone(@RequestParam ("commentId") Long commentId,
                            @RequestParam ("replyBody") String replyBody){
        if (commentId == null || commentId< 1 || StringUtils.isEmpty(replyBody)){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (commentService.reply(commentId,replyBody)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("回复失败");
        }
    }

    /**
     * 删除评论的接口
     */
    @GetMapping("/comment/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length<1){
            return ResultGenerator.genFailResult("参数异常！");
        }

        if (commentService.deleteBetch(ids)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("删除失败！");
        }

    }

}
