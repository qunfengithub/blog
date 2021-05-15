package com.feng.blog.controller.admin;

import com.feng.blog.entity.Blog;
import com.feng.blog.service.BlogService;
import com.feng.blog.service.CategoryService;
import com.feng.blog.util.MyBlogUtils;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.Result;
import com.feng.blog.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.scanner.Constant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;


/**
 * 后台管理系统中博客文章模块到这里就暂时告一段落了，这个模块的讲解总共用了三个实验，因为这个模块确实是整个系统中最重要的部分
 */
@Controller
@RequestMapping("/admin")
public class BlogController {


    @Resource
    private BlogService blogService;

    @Resource
    private CategoryService categoryService;

    //博客进行展示   文章列表接口
    //列表接口负责接收前端传来的分页参数，如 page 、limit 等参数，之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端
    @GetMapping("/blogs/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(blogService.getBlogsPage(pageUtil));
    }


    //点击到编辑页面  跳转到编辑的页面
    //点击后的跳转路径为 /admin/blogs/edit，之后我们新建 Controller 来处理该路径并跳转到对应的页面。
    @GetMapping("/blogs/edit")
    public String edit (HttpServletRequest request){

        //该方法用于处理 /admin/blogs/edit 请求，并设置 path 字段，之后跳转到 admin 目录下的 edit.html 中。
        request.setAttribute("path","edit");
        request.setAttribute("categories",categoryService.getAllCategories());
        return "admin/edit";
    }

    //跳转到一个id
    @GetMapping("/blogs/edit/{blogId}")
    public String edit(HttpServletRequest request,@PathVariable("blogId")Long blogId){
        request.setAttribute("path","edit");
        Blog blog=blogService.getBlogById(blogId);
        if (blog ==null) {
            return "error/error_404";//404页面
        }
        //
        request.setAttribute("blog",blog);
        request.setAttribute("categories",categoryService.getAllCategories());
        return "admin/edit";
    }


    @GetMapping("/blogs")
    public String list(HttpServletRequest request){
        request.setAttribute("path","blogs");
        return "admin/blog";
    }


//    @GetMapping("/blogs/edit")
//    public String edit (HttpServletRequest request){
//        request.setAttribute("path","edit");
//        return "admin/edit";
//    }



    //在blog 中添加文件上传的功能
    @PostMapping("/blogs/md/uploadfile")
    public void uploadFileByEditormd(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(name = "editormd-image-file",required = true)MultipartFile file) throws URISyntaxException, IOException {
        String FILE_UPLOAD_DIC="/home";  //根据上传的目录的位置去进行上传

        //获取原始文件的名字名字
        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf("."));

        //生成文件名  随机+ 日期的
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random random = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(random.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();

        //创建文件
        File destFile = new File(FILE_UPLOAD_DIC + newFileName);
        //上传地址的路径
        String fileUrl = MyBlogUtils.getHost(new URI(request.getRequestURI() + " ")) + "/upload/" + newFileName;
        File fileDirectory = new File(FILE_UPLOAD_DIC);

       try {

        if (!fileDirectory.exists()){//不存在
            //创建文件夹
            if (!fileDirectory.mkdir()){
                throw new IOException("文件夹创建失败，路径为："+fileDirectory);
            }
        }
        file.transferTo(destFile);
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type","text/html");
        response.getWriter().write("{\"success\": 1, \"message\":\"success\",\"url\":\"" + fileUrl + "\"}");

       } catch (UnsupportedEncodingException e) {
           response.getWriter().write("{\"success\":0}");
       } catch (IOException e) {
           response.getWriter().write("{\"success\":0}");
       }
    }


    //将博客进行保存   分类和标签id  文章内容
    @PostMapping("/blogs/save")
    @ResponseBody
    public Result save(@RequestParam("blogTitle")String blogTitle,
                      @RequestParam (name = "blogSubUrl",required = false) String blogSubUrl,
                       @RequestParam("blogCategoryId") Integer blogCategoryId,
                       @RequestParam("blogTags") String blogTags,
                       @RequestParam("blogContent") String blogContent,
                       @RequestParam("blogCoverImage") String blogCoverImage,
                       @RequestParam("blogStatus") Byte blogStatus,
                       @RequestParam("enableComment") Byte enableComment){

        if (StringUtils.isEmpty(blogTitle)){
            return ResultGenerator.genFailResult("请输入文章标题");
        }
        if (blogTitle.trim().length() >150){
            return ResultGenerator.genFailResult("标题太长");
        }
        //标签又是一个数据库
        if (StringUtils.isEmpty(blogTags)){
            return ResultGenerator.genFailResult("请输入标题标签");
        }

        if (blogTags.trim().length() >150){
            return ResultGenerator.genFailResult("标签太长");
        }

        if(blogSubUrl.trim().length()>150){
            return ResultGenerator.genFailResult("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)){
            return ResultGenerator.genFailResult("请输入文章内容");
        }
        if (blogContent.trim().length()>10000){
            return ResultGenerator.genFailResult("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)){
            return ResultGenerator.genFailResult("文章封面图不能为空");
        }

        //创建博客
        Blog blog=new Blog();
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String saveBlogResult =blogService.saveBlog(blog);
        if ("success".equals(saveBlogResult)){
            return ResultGenerator.genSuccessResult("添加成功");
        }else {
            return ResultGenerator.genFailResult(saveBlogResult);
        }


    }


    //修改博客的
    @PostMapping("/blogs/update")
    @ResponseBody
    public Result update(@RequestParam ("blogId") Long blogId,
                         @RequestParam("blogTitle") String blogTitle,
                         @RequestParam(name = "blogSubUrl",required = false) String blogSubUrl,
                         @RequestParam("blogCategoryId") Integer blogCategoryId,
                         @RequestParam("blogTags") String blogTags,
                         @RequestParam("blogContent") String blogContent,
                         @RequestParam("blogCoverImage") String blogCoverImage,
                         @RequestParam("blogStatus") Byte blogStatus,
                         @RequestParam("enableComment") Byte enableComment){

        if (StringUtils.isEmpty(blogTitle)){
            return ResultGenerator.genFailResult("请输入文章标题");
        }
        if (blogTitle.trim().length() >150){
            return ResultGenerator.genFailResult("标题太长");
        }
        //标签又是一个数据库
        if (StringUtils.isEmpty(blogTags)){
            return ResultGenerator.genFailResult("请输入标题标签");
        }

        if (blogTags.trim().length() >150){
            return ResultGenerator.genFailResult("标签太长");
        }

        if(blogSubUrl.trim().length()>150){
            return ResultGenerator.genFailResult("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)){
            return ResultGenerator.genFailResult("请输入文章内容");
        }
        if (blogContent.trim().length()>10000){
            return ResultGenerator.genFailResult("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)){
            return ResultGenerator.genFailResult("文章封面图不能为空");
        }

        Blog blog=new Blog();
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String updateBlogResult =blogService.updateBlog(blog);

        if ("success".equals(updateBlogResult)){
            return ResultGenerator.genSuccessResult("添加成功");
        }else {
            return ResultGenerator.genFailResult(updateBlogResult);
        }


    }

    @PostMapping("/blogs/delete")
    @ResponseBody
    public Result delete (@RequestBody Integer[] ids){
        //验证是否 少于1个
        if (ids.length <1 ){
            return ResultGenerator.genFailResult("参数异常");
        }

        if (blogService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult("删除失败");
        }

    }




}
