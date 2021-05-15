package com.feng.blog.service.impl;

import com.feng.blog.dao.BlogCommentMapper;
import com.feng.blog.entity.BlogComment;
import com.feng.blog.service.CommentService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;
import com.feng.blog.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    //@Resource
    @Autowired
    BlogCommentMapper blogCommentMapper;

    @Override
    public PageResult getCommentPage(PageQueryUtil pageUtil) {

        //获取评论的列表
        List<BlogComment> comments = blogCommentMapper.findBlogCommentList(pageUtil);
        //获取评论的数量
        int total = blogCommentMapper.getTotalBlogComments(pageUtil);
        //分页 的 设置
        PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());

        return pageResult;
    }


    //审核评论  审核评论就是修改 comment_status的状态为1
    @Override
    public Boolean checkDone(Integer[] ids) {
        //实际就是修改状态为1
        return blogCommentMapper.checkDone(ids)>0;
    }


    //回复的接口
    @Override
    public Boolean reply(Long commentId, String replyBody) {
        //查询出回复的的数据
        BlogComment blogComment = blogCommentMapper.selectByPrimaryKey(commentId);

        //blogComment 不为空 并且已经审核完成  intValue() 把String类型的变成int 类型的
        if (blogComment !=null && blogComment.getCommentStatus().intValue() ==1){
            //放入回复的内容
            blogComment.setCommentBody(replyBody);
            //设置回复的当前的时间
            blogComment.setReplyCreateTime(new Date());
            return blogCommentMapper.updateByPrimaryKeySelective(blogComment) >0 ;
        }
        return false;
    }


    //删除评论的接口   Ctrl+Shift+P   实现接口中的方法
    @Override
    public Boolean deleteBetch(Integer[] ids) {

        return blogCommentMapper.deleteBatch(ids) >0;
    }


    @Override
    public Boolean addComment(BlogComment comment) {
        return blogCommentMapper.insertSelective(comment) >0;
    }

    @Override
    public PageResult getCommentPageByBlogIdAndPageNum(long blogId, Integer page) {
        if (page <1 ){
            return null;
        }

        Map param=new HashMap();
        param.put("page",page);

        //每页8条数据
        param.put("limit",8);
        param.put("blogId",blogId);// 过滤当前评论的数据
        param.put("commentStatus",1);//评论的状态
        PageQueryUtil pageUtil = new PageQueryUtil(param);
        //查找到所有的评论
        List<BlogComment> comments = blogCommentMapper.findBlogCommentList(param);

        if (!CollectionUtils.isEmpty(comments)){
            int total = blogCommentMapper.getTotalBlogComments(pageUtil);
            PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
            return pageResult;
        }


        return null;
    }
}
