package com.feng.blog.controller.admin;

import com.feng.blog.service.CategoryService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.Result;
import com.feng.blog.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 分类的模块在后端有5个接口
 * 1.分类列表分页接口
 * 2.添加分类的接口
 * 3.根据id获取单条分类记录接口
 * 4.修改分类的接口
 * 5.删除分类的接口
 * 列表接口负责接收前端传来的分页参数，如 page 、limit 等参数，之后将数据总数和对应页面的数据列表查询出来
 * 并封装为分页数据返回给前端
 *
 *
 */

@Controller
@RequestMapping("/admin")
public class CategoryController {


    @Resource
    CategoryService categoryService;


    //分类列表
    @RequestMapping(value = "/categories/list",method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {

        //page 分页 和limit 的限制
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        //有一个分页的工具类
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(categoryService.getBlogCategoryPage(pageUtil));
    }


    /**
     * 分类添加  添加分类的接口
     *
     */
    @RequestMapping(value = "/categories/save" ,method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestParam("categoryName") String categroyName,
                       @RequestParam("categoryIcon") String categoryIcon){
        if (StringUtils.isEmpty(categroyName)){
            return ResultGenerator.genFailResult("分类名称不能为空");
        }

        if (StringUtils.isEmpty(categoryIcon)){
            return ResultGenerator.genFailResult("分类图标不能为空");
        }

        if (categoryService.saveCategory(categroyName,categoryIcon)){
            return ResultGenerator.genSuccessResult("保存成功");
        }else {
            return ResultGenerator.genFailResult("分类名称重复");
        }
    }

    /**
     * 分类修改
     */
    @RequestMapping(value = "/categories/update" ,method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestParam ("categoryId") Integer categoryId,
                         @RequestParam ("categoryName") String categoryName,
                         @RequestParam("categoryIcon") String categoryIcon){

        if(StringUtils.isEmpty(categoryName)){
            return ResultGenerator.genFailResult("请输入分类名称！");
        }

        if (StringUtils.isEmpty(categoryIcon)){
            return ResultGenerator.genFailResult("请选择分类图标");
        }
        if (categoryService.updateCategory(categoryId, categoryName, categoryIcon)) {
             return ResultGenerator.genSuccessResult("更新成功");
        }else {
             return ResultGenerator.genFailResult("更新失败");
        }

    }


    /**
     * 分类删除
     * @RequestBody 将前端传过来的参数封装为 id 数组  Json字符串
     */
    @RequestMapping(value = "/catagories/delete",method = RequestMethod.POST)
    @ResponseBody
    public Result delete (@RequestBody Integer[] ids){
        //进行参数的验证
        if (ids.length <1){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (categoryService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }









}
