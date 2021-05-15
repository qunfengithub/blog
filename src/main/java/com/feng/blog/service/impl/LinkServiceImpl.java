package com.feng.blog.service.impl;

import com.feng.blog.dao.BlogLinkMapper;
import com.feng.blog.entity.BlogLink;
import com.feng.blog.service.LinkService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {

    @Autowired
    BlogLinkMapper blogLinkMapper; //博客链接


    /**
     * list – 列表数据
     * totalCount – 总记录数
     * pageSize – 每页记录数
     * currPage – 当前页数
     * @param pageUtil
     * @return
     */
    @Override
    public PageResult getBlogLinkPage(PageQueryUtil pageUtil) {
        //获取到 所有数据 集合
        List<BlogLink> links = blogLinkMapper.findLinkList(pageUtil);
        //获取到 数量
        int total1 = blogLinkMapper.getTotalLinks(pageUtil);
        PageResult pageResult = new PageResult(links, total1, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }



    //保存友情链接
    @Override
    public Boolean saveLink(BlogLink link) {

        //插入新的友情链接
        return blogLinkMapper.insertSelective(link)>0;
    }

    //删除友链
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return blogLinkMapper.deleteBatch(ids) >0;
    }


    //通过id  查找到 详情页
    @Override
    public BlogLink selectById(Integer id) {

        return blogLinkMapper.selectByPrimaryKey(id);
    }


    //修改链接
    @Override
    public Boolean updateLink(BlogLink tempLink) {

        return blogLinkMapper.updateByPrimaryKeySelective(tempLink) >0;
    }
}
