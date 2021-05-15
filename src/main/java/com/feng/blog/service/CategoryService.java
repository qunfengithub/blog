package com.feng.blog.service;

import com.feng.blog.entity.BlogCategory;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;

import java.util.List;

public interface CategoryService {

    PageResult getBlogCategoryPage(PageQueryUtil pageUtil);

    int getTotalCategories();

    Boolean saveCategory(String categoryName, String categoryIcon);

    Boolean updateCategory(Integer categoryId, String categoryName, String categoryIcon);

    Boolean deleteBatch(Integer[] ids);

    List<BlogCategory> getAllCategories();

}
