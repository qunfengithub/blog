package com.feng.blog.controller.admin;


import com.feng.blog.entity.BlogLink;
import com.feng.blog.service.LinkService;
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
public class LinkController {


    @Autowired
    LinkService linkService;

    //导航栏链接
    //该方法用于处理 /admin/links 请求，并设置 path 字段，之后跳转到 admin 目录下的 link.html 中。
    @GetMapping("/links")
    public String linkPage(HttpServletRequest request){
        request.setAttribute("path","links");
        return "admin/links";
    }


    /**
     * 友链模块在后台管理系统中有 5 个接口，分别是：
     *
     * 友链列表分页接口
     * 添加友链接口
     * 根据 id 获取单条友链记录接口
     * 修改友链接口
     * 删除友链接口
     */

    //友情列表  分页列表的参数  页数 分页
    @GetMapping("/links/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常！");
        }

        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(linkService.getBlogLinkPage(pageUtil));

    }

    //添加好友的链接口  友情添加

    /**
     *
     * @param linkType 友链类型
     * @param linkName 友链名称
     * @param linkUrl 友链的跳转链接
     * @param linkRank 排序值
     * @param linkDescription 友链简介
     * @return
     */
    @RequestMapping(value = "/links/save",method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestParam ("linkType") Integer linkType,
                       @RequestParam ("linkName") String linkName,
                       @RequestParam ("linkUrl") String linkUrl,
                       @RequestParam ("linkRank") Integer linkRank,
                       @RequestParam ("linkDescription")String  linkDescription){
        //有参数都要判断非空验证
        if (linkType == null || linkType < 0 || linkRank == null || linkRank < 0  || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkUrl) || StringUtils.isEmpty(linkDescription)) {
            return ResultGenerator.genFailResult("参数异常");
        }

        BlogLink link = new BlogLink();
        link.setLinkType(linkType.byteValue()); //返回Byte 对象的值，该值转换成byte类型
        link.setLinkRank(linkRank);//排序值
        link.setLinkName(linkName);//链接的名字
        link.setLinkDescription(linkDescription);//简介
        //把数值放入link 类里面 然后保存
        return ResultGenerator.genSuccessResult(linkService.saveLink(link));
    }


    //友链的删除
    @RequestMapping(value = "links/delete",method = RequestMethod.POST)
    @ResponseBody  //响应
    public Result delete (@RequestParam Integer [] ids){
        if (ids.length <1){
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (linkService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("删除失败");
        }

    }


    //详情页面
    @GetMapping("/links/info/{id}")
    @ResponseBody
    public Result info(@PathVariable ("id") Integer id){
        //根据id 查询到 链接
        BlogLink link  =linkService.selectById(id);
        return ResultGenerator.genSuccessResult(link);

    }


    //友链的修改  通过id  去修改的
    @RequestMapping(value = "/link/update",method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestParam ("linkId") Integer linkId,
                         @RequestParam ("linkType") Integer linkType,
                         @RequestParam ("linkName") String linkName,
                         @RequestParam ("linkUrl") String linkUrl,
                         @RequestParam ("linkRank") Integer linkRank,
                         @RequestParam ("linkDescription")String  linkDescription){

        //先去查询，然后判断非空 ，然后设置吧修改的值放入里面
        BlogLink tempLink = linkService.selectById(linkId);

        if (tempLink ==null){
            return ResultGenerator.genFailResult("无数据");
        }
        //验证参数非空的情况
        if (linkType == null || linkType < 0 || linkRank == null || linkRank < 0  || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkUrl) || StringUtils.isEmpty(linkDescription)) {
            return ResultGenerator.genFailResult("参数异常");
        }
        tempLink.setLinkType(linkType.byteValue());
        tempLink.setLinkName(linkName);
        tempLink.setLinkUrl(linkUrl);
        tempLink.setLinkRank(linkRank);
        tempLink.setLinkDescription(linkDescription);
        //返回修改后的程序
        return ResultGenerator.genSuccessResult(linkService.updateLink(tempLink));

    }







}
