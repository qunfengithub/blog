package com.feng.blog.service.impl;

import com.feng.blog.dao.BlogCategoryMapper;
import com.feng.blog.dao.BlogMapper;
import com.feng.blog.entity.BlogCategory;
import com.feng.blog.service.CategoryService;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private BlogCategoryMapper blogCategoryMapper;

    @Autowired
    private BlogMapper blogMapper;



    //获取到博客的页数
    @Override
    public PageResult getBlogCategoryPage(PageQueryUtil pageUtil) {
        //查询到
        List<BlogCategory> categoryList = blogCategoryMapper.findCategoryList(pageUtil);
        int total = blogCategoryMapper.getTotalCategories(pageUtil);
        PageResult pageResult = new PageResult(categoryList, total, pageUtil.getLimit(), pageUtil.getPage());

        return pageResult;
    }

    @Override
    public int getTotalCategories() {
        return 0;
    }

    //添加分类的
    @Override
    public Boolean saveCategory(String categoryName, String categoryIcon) {
        //实体类  查询到实体类     根据分类名查询 查询到分类
        //1.首先会根据名称查询是否已经存在该分类
        BlogCategory temp = blogCategoryMapper.selectByCategoryName(categoryName);
        //查看temp的类
        if (temp ==null){
            //2.之后才会进行数据封装并进行数据库 insert 操作。
            BlogCategory blogCategory = new BlogCategory();
            blogCategory.setCategoryName(categoryName);
            blogCategory.setCategoryIcon(categoryIcon);
            //插入一个实体类的  blog
            return blogCategoryMapper.insertSelective(blogCategory) >0 ;
        }
        //返回的名称已经重复
        return false;
    }

    //修改目录的
    @Override
    public Boolean updateCategory(Integer categoryId, String categoryName, String categoryIcon) {

        //查询出实体类的情况
        BlogCategory blogCategory = blogCategoryMapper.selectByPrimaryKey(categoryId);
        //判断非空
        if (blogCategory !=null){
            //是空的情况下 把 分类的写入里面
            //把 博客的分类写入到分类中
            blogCategory.setCategoryName(categoryName);
            blogCategory.setCategoryIcon(categoryIcon);

            //  博客的分类的名称的修改  修改实体类
         blogMapper.updateBlogCategorys(categoryName,blogCategory.getCategoryId(),new Integer[]{categoryId});
         return blogCategoryMapper.updateByPrimaryKeySelective(blogCategory)>0;
        }

        return false;
    }

    //删除ids
    @Override
    public Boolean deleteBatch(Integer[] ids) {

        //先判断ids  是不是null  不为null 执行删除的ids
        if (ids.length < 1){
            return false;
        }
        //修改tb_blog 表
        blogMapper.updateBlogCategorys("默认分类",0,ids);
        //删除分类的数据
        return  blogCategoryMapper.deleteBatch(ids)>0;
    }

    //获取所有的目录
    @Override
    public List<BlogCategory> getAllCategories() {
        return blogCategoryMapper.findCategoryList(null);
    }
}
