package com.feng.blog.entity;


/**
 * 标签栏  点击标签 跳转到对应的标签下的博客列表
 * 标签的主键列表  ，返回的数据 如下定义
 */
public class BlogTagCount {

    private Integer tagId; //标签的主键

    private String tagName;//标签的名字

    private Integer tagCount;//标签的数量


    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getTagCount() {
        return tagCount;
    }

    public void setTagCount(Integer tagCount) {
        this.tagCount = tagCount;
    }
}
