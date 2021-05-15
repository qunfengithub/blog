package com.feng.blog.service;

import com.feng.blog.controller.vo.BlogDetailVO;
import com.feng.blog.controller.vo.SimpleBlogListVO;
import com.feng.blog.entity.Blog;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;

import java.util.List;

public interface BlogService {

    /**
     * 根据id  获取到详情
     * @param blogId
     * @return
     */
    Blog getBlogById(Long blogId);

    /**
     * 获取到文章列表接口的
     * @param pageUtil
     * @return
     */
    PageResult getBlogsPage(PageQueryUtil pageUtil);

    String saveBlog(Blog blog);

    String updateBlog(Blog blog);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    Boolean deleteBatch(Integer[] ids);

    /**
     * 根据搜索获取文章列表
     *
     * @param keyword
     * @param page
     * @return
     */
    PageResult getBlogsPageBySearch(String keyword, int page);

    /**
     * 首页侧边栏数据列表
     * 0-点击最多 1-最新发布
     *
     * @param type
     * @return
     */
    List<SimpleBlogListVO> getBlogListForIndexPage(int type);

    /**
     * 获取首页文章列表
     * @param page
     * @return
     */
    PageResult getBlogsForIndexPage(int page);

    /**
     * 根据分类获取到首页文章列表
     * @param categoryName
     * @param page
     * @return
     */
    PageResult getBlogsPageByCategory(String categoryName, int page);


    /**
     * 根据标签页获取首页的文章列表
     * @param tagName
     * @param page
     * @return
     */
    PageResult getBlogsPageByTag(String tagName, int page);


    /**
     * 文章详情页的获取
     * @param blogId
     * @return
     */
    BlogDetailVO getBlogDetail(Long blogId);
}
