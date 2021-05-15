package com.feng.blog.service;

import com.feng.blog.entity.BlogLink;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;

public interface LinkService {

    PageResult getBlogLinkPage(PageQueryUtil pageUtil);

    Boolean saveLink(BlogLink link);

    Boolean deleteBatch(Integer[] ids);

    BlogLink selectById(Integer id);

    //修改返回的 1
    Boolean updateLink(BlogLink tempLink);
}
