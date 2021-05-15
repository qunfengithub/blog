package com.feng.blog.controller.vo;

import java.io.Serializable;

/**
 * 数据格式的定义
 *
 * 知识点 ，最新发布 ，点击最多，文章列表， 分页功能
 *
 * 我们通过一个博客 只能看出一个标题的字段，  点击标题可以跳转到相应的博客页面
 * 因此我们需要一个博客实体的id字段
 * 通过图片我们只能看出一个博客标题字段，但是这里通常会设计成可跳转的形式，
 * 即点击标题后会跳转到对应的博客详情页面中，因此还需要一个博客实体的 id 字段，
 * 因此返回数据的格式就得出来了
 *
 *
 * 博客的Id 和 博客标题   点击标题可以跳转
 */
public class SimpleBlogListVO implements Serializable {

    private Long blogId;
    private String blogTitle;

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }
}
