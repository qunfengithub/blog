package com.feng.blog.service;

import com.feng.blog.entity.BlogComment;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;

public interface CommentService {

    PageResult getCommentPage(PageQueryUtil pageUtil);

    Boolean checkDone(Integer[] ids);

    Boolean reply(Long commentId, String replyBody);

    Boolean deleteBetch(Integer[] ids);

    Boolean addComment(BlogComment comment);

    PageResult getCommentPageByBlogIdAndPageNum(long blogId, Integer commentPage);
}
