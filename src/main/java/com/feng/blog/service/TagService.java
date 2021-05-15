package com.feng.blog.service;

import com.feng.blog.entity.BlogTagCount;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;

import java.util.List;

public interface TagService {



    Boolean saveTag(String tagName);

    Boolean deleteBatch(Integer[] ids);

    PageResult getBlogTagPage(PageQueryUtil pageUtil);

    /**
     * 实现标签数据的查询 接口
     */
    List<BlogTagCount> getBlogTagCountForIndex();

}
