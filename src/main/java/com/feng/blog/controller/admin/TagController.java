package com.feng.blog.controller.admin;

import com.feng.blog.service.TagService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.Result;
import com.feng.blog.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 侧边导航栏的方法
 *
 */
@Controller
@RequestMapping("/admin")
public class TagController {

    @Resource
    private TagService tagService;


    @GetMapping("/tags")
    public String tagPage(HttpServletRequest request){
        request.setAttribute("path","tags");
        return "admin/tag";
    }


    //显示列表的

    /**
     * 列表接口负责接收前端传来的分页参数，如 page 、limit 等参数，
     * 之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端。
     * @param params
     * @return
     */
    @GetMapping("/tags/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        //判断非空
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);

        return ResultGenerator.genSuccessResult(tagService.getBlogTagPage(pageUtil));
    }

    //添加标签接口
    @PostMapping("/tags/save")
    @ResponseBody
    public Result save(@RequestParam("tagName") String tagName){
        if (StringUtils.isEmpty(tagName)){
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (tagService.saveTag(tagName)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("标签名重复");
        }
    }


    @PostMapping("/tags/delete")
    @ResponseBody
    public Result delete (@RequestBody Integer[] ids){
        if (ids.length <1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (tagService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("有关数据请勿强行删除");
        }
    }





}
