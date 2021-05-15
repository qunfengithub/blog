package com.feng.blog.service.impl;

import com.feng.blog.dao.BlogTagMapper;
import com.feng.blog.dao.BlogTagRelationMapper;
import com.feng.blog.entity.BlogTag;
import com.feng.blog.entity.BlogTagCount;
import com.feng.blog.service.TagService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class TagServiceImpl  implements TagService {


    @Resource
    BlogTagMapper blogTagMapper;

    @Resource
    BlogTagRelationMapper relationMapper;



    //保存标签的
    @Override
    public Boolean saveTag(String tagName) {
        //都是先查询然后再 插入的
        BlogTag temp = blogTagMapper.selectByTagName(tagName);

        if (temp==null){
            BlogTag blogTag = new BlogTag();
            blogTag.setTagName(tagName);
            return blogTagMapper.insertSelective(blogTag)>0;
        }

        return false;
    }


    //批量删除的
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        //我们需要判断该标签是否已经与文章表中的数据进行了关联，如果已经存在关联关系，就不进行删除操作
        List<Long> relations = relationMapper.selectDistinctTagIds(ids);
        if (!CollectionUtils.isEmpty(relations)){ //判断非空
            return false;
        }
        //删除tag
        return blogTagMapper.deleteBatch(ids) >0;
    }

    //获取到Page的页面

    /**
     * 接收到前端传来的参数
     * 之后将数据总数和对应页面的数据列表查询出来并封装为分页数据返回给前端。
     * @param pageUtil
     * @return
     */
    @Override
    public PageResult getBlogTagPage(PageQueryUtil pageUtil) {
        //根据传来的参数 查询到分页数据的集合
        List<BlogTag> tags = blogTagMapper.findTagList(pageUtil);
        int total = blogTagMapper.getTotalTags(pageUtil);
        //分页的数据返回给前端
        PageResult pageResult = new PageResult(tags, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;//返回给页面的结果
    }


    /**
     * 点击标签后会跳转到对应标签栏下面的博客列表
     * @return
     */
    @Override
    public List<BlogTagCount> getBlogTagCountForIndex() {

        //返回的数据格式 BlogTagCount 这样的
        //查询的标签的数量    难点在SQL语句上面
        return blogTagMapper.getTagCount();
    }


}
