package com.feng.blog.service.impl;

import com.feng.blog.controller.vo.BlogDetailVO;
import com.feng.blog.controller.vo.BlogListVO;
import com.feng.blog.controller.vo.SimpleBlogListVO;
import com.feng.blog.dao.*;
import com.feng.blog.entity.Blog;
import com.feng.blog.entity.BlogCategory;
import com.feng.blog.entity.BlogTag;
import com.feng.blog.entity.BlogTagRelation;
import com.feng.blog.service.BlogService;
import com.feng.blog.util.MarkDownUtil;
import com.feng.blog.util.PageQueryUtil;
import com.feng.blog.util.PageResult;
import com.feng.blog.util.PatternUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl  implements BlogService {

    @Autowired
     BlogMapper blogMapper;//博客的数据

    @Autowired
    BlogCategoryMapper categoryMapper;//博客目录的

    @Autowired
    BlogTagMapper  tagMapper;//博客标签

    @Autowired
    BlogTagRelationMapper blogTagRelationMapper;




    /**
     * 跟据id  查询到博客的详情的
     * @param blogId
     * @return
     */
    @Override
    public Blog getBlogById(Long blogId) {
        return blogMapper.selectByPrimaryKey(blogId);
    }


    //分页的结果
    @Override
    public PageResult getBlogsPage(PageQueryUtil pageUtil) {
        List<Blog> blogList = blogMapper.findBlogList(pageUtil);
        int total = blogMapper.getTotalBlogs(pageUtil);
        PageResult pageResult = new PageResult(blogList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }



    //保存文章的   业务逻辑层
    //在新增文章实体的方法中 比前文中的新增加的方法比较起来 要复杂一些 ，因为前面都是单表操作的，并不涉及到表的操作，
    //而文章的表与分类的表有关联，因此在新增文章内容的时候需要对 其他表进行查询 和修改的操作，对于分类表只是查询和验证，对于标签则需要查询
    //和新增操作，因为标签是在文章编辑页面输入的，如果某些标签内容是标签表中没有的则需要更新增，之后操作文章标签关系表。
    //将文章与标签关联起来并新增关系表中
    //分类表只是查询和验证，对于标签表则需要查询和新增操作，因为标签是在文章编辑页面输入的，如果某些标签内容是标签表中没有的则需要新增，之后会操作文章标签关系表，将文章与标签关联起来并新增至关系表中，
    @Override
    @Transactional  //开启事务
    public String saveBlog(Blog blog) {
        //查询到博客的分类
        BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
        //判断分类为空
        if (blogCategory == null) {
            blog.setBlogCategoryId(0);
            blog.setBlogCategoryName("默认分类");
        } else {
            //不为null  设置分类的名称
            //设置博客分类的名称
            blog.setBlogCategoryName(blogCategory.getCategoryName());
            //分类的排序增加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
        }

        //处理标签数据   从传入参数获取到 获取博客所有的标签
        String[] tags = blog.getBlogTags().split(","); //变成数组
        //标签的数量不能大于6
        if (tags.length > 6) {
            return "标签数量限制为6";
        }

        //保存文章      插入数据>0
        if (blogMapper.insertSelective(blog) > 0) {
            // 创建插入标签的集合
            List<BlogTag> tagListForInsert = new ArrayList<>();
            // 创建所有的Tag集合，   用于建立关系数据
            List<BlogTag> allTagList = new ArrayList<>();
            //tag 获取的数量  for循环tags 标签
            for (int i = 0; i < tags.length; i++) {
                //根据标签的名字查询到  查询到博客标签
                BlogTag tag = tagMapper.selectByTagName(tags[i]);
                //标签为null   要插入的标签为null
                if (tag == null) {
                    //创建标签
                    BlogTag tempTag = new BlogTag();
                    //写入标签名
                    tempTag.setTagName(tags[i]);
                    //把实体类添加  插入的集合中
                    tagListForInsert.add(tempTag);
                } else {
                    allTagList.add(tag);
                }
            }
            //新增标签数据并修改分类排序值
            if (!CollectionUtils.isEmpty(tagListForInsert)) {
                tagMapper.batchInsertBlogTag(tagListForInsert);
            }
            //修改分类  根据博客分类写入到里面
            categoryMapper.updateByPrimaryKeySelective(blogCategory);
            List<BlogTagRelation> blogTagRelations = new ArrayList<>();
//对于标签表则需要查询和新增操作，因为标签是在文章编辑页面输入的，如果某些标签内容是标签表中没有的则需要新增，之后会操作文章标签关系表，将文章与标签关联起来并新增至关系表中，
            //新增关系数据   添加到标签
            allTagList.addAll(tagListForInsert);
            //遍历所有的
            for (BlogTag tag : allTagList) {
                //博客和标签的关系
                BlogTagRelation blogTagRelation = new BlogTagRelation();
                //写入BlogId
                blogTagRelation.setBlogId(blog.getBlogId());
                //写入TagId
                blogTagRelation.setTagId(tag.getTagId());
                //写入博客关系
                blogTagRelations.add(blogTagRelation);
            }

            //插入博客关系
            if (blogTagRelationMapper.batchInsert(blogTagRelations) > 0) {
                return "success";
            }
        }

        return "保存失败";

    }

    /**
     * 文章的修改  可以根据文章的保存有点像
     * @param blog
     * @return
     */

    @Override
    @Transactional
    public String updateBlog(Blog blog) {
        //先根据博客的id 查询到 博客
        Blog blogForUpdate = blogMapper.selectByPrimaryKey(blog.getBlogId());
        if (blogForUpdate ==null){
            return "数据不存在";
        }
        //把内容放到新博客里面   分类和标签 需要和数据库进行关联
        blogForUpdate.setBlogTitle(blog.getBlogTitle());
        blogForUpdate.setBlogSubUrl(blog.getBlogSubUrl());
        blogForUpdate.setBlogContent(blog.getBlogContent());
        blogForUpdate.setBlogCoverImage(blog.getBlogCoverImage());
        blogForUpdate.setBlogStatus(blog.getBlogStatus());
        blogForUpdate.setEnableComment(blog.getEnableComment());
        //根据博客的id 查询到博客的目录
        BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());

        //判断目录为null 的情况 ，
        if (blogCategory == null) {
            blogForUpdate.setBlogCategoryId(0);//设置分类的目录为 0
            blogForUpdate.setBlogCategoryName("默认分类");
        }else{
            //设置目录的分类 名称
            blogForUpdate.setBlogCategoryName(blogCategory.getCategoryName()); //从修改中获取到 目录的名字
            blogForUpdate.setBlogCategoryId(blogCategory.getCategoryId());

            //分类的排序加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() +1);
        }

        //处理标签数据  标签的数据跟数据博客之间的数据的关系
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length>6){
            return "标签数量限制为6";  //
        }
        //还原原来有多少个标签
        blogForUpdate.setBlogTags(blog.getBlogTags());

        //新增加 修改的标签
        ArrayList<BlogTag> tagListForInsert = new ArrayList<>();
        //所有的tag对象，用于建立 关系数据
        ArrayList<BlogTag> allTagsList = new ArrayList<>();

        for (int i = 0; i < tags.length; i++) {  //遍历原来的标签
            BlogTag tag = tagMapper.selectByTagName(tags[i]);//遍历查询到原来 标签 的名字
            if (tag == null){ //如果原来的 没有标签
                //不存在标签就新增加标签
                BlogTag tempTag = new BlogTag();//创建一个标签
                tempTag.setTagName(tags[i]); //新增加标签的名字
                tagListForInsert.add(tempTag); //把中间的标签 放入插入的标签
            }else{
                allTagsList.add(tag); //把遍历查询到的标签 放入所有的  allTag中
            }
        }

        //新增标签数据不为空  --> 新增标签数据
        //判断新增加集合 的数据 是否为 null
        if (!CollectionUtils.isEmpty(tagListForInsert)){
            tagMapper.batchInsertBlogTag(tagListForInsert);  //批量插入到新增数据
        }

        //博客标签 关系
        ArrayList<BlogTagRelation> blogTagRelations = new ArrayList<>();

        //新增关系数据
        allTagsList.addAll(tagListForInsert); //把新增加的数据插入到 所有数据里面
        for (BlogTag tag : allTagsList) { //遍历所有的标签
            BlogTagRelation blogTagRelation = new BlogTagRelation();
            blogTagRelation.setBlogId(blog.getBlogId()); //博客的id
            blogTagRelation.setTagId(tag.getTagId());
            blogTagRelations.add(blogTagRelation);// 博客关系添加上去
        }

        //修改blog 信息 修改分类排序值 --》删除原有数据 --》保存新的关系数据
        categoryMapper.updateByPrimaryKeySelective(blogCategory);
        //删除原有关系数据
        blogTagRelationMapper.deleteByBlogId(blog.getBlogId());
        blogTagRelationMapper.batchInsert(blogTagRelations);//插入新的关系
        if (blogMapper.updateByPrimaryKeySelective(blogForUpdate) >0){
            return "success";
        }
        return "修改失败";

        
    }

    /**
     * 批量删除的id
     * @param ids
     * @return
     */
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1){
            return false;
        }
        //删除成功
        return blogMapper.deleteBatch(ids)>0;
    }

    /**
     * 数据查询
     *
     * 首页侧边栏数据列表
     * 0 -点击最多  1-最新发布
     *type 等于 0 时为查询点击最多的博客列表，type 等于 1 时为查询最新发布的博客列表，
     * 返回的数据格式为 SimpleBlogListVO
     *
     * 首先根据 type 字段的不同去查询对应的博客列表，但是查询出来的数据类型为 Blog，
     * 之后将 Blog 类型的数据转换为 SimpleBlogListVO 并返回即可，
     */
    public List<SimpleBlogListVO> getBlogListForIndexPage(int type){

        ArrayList<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        //在数据库中查询
        List<Blog> blogs = blogMapper.findBlogListByType(type, 9);
        // 判断集合是否为null
        if (!CollectionUtils.isEmpty(blogs)){
            for (Blog blog : blogs) {
                SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
                //查询出来的数据类型为 Blog，之后将 Blog 类型的数据转换为 SimpleBlogListVO
                BeanUtils.copyProperties(blog,simpleBlogListVO);
                simpleBlogListVOS.add(simpleBlogListVO);//添加上

            }

        }
        return simpleBlogListVOS;

    }


    /**
     * 获取首页文章的列表
     * @param page
     * @return
     */
    @Override
    public PageResult getBlogsForIndexPage(int page) {
        //创建Map 集合
        Map params = new HashMap();
        params.put("page",page);
        //每页 8条数据
        params.put("limit",8);
        params.put("blogStatus",1);//过滤发布状态下的数据
        //分页的工具类
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        //查询的数据 进行放入分页的   集合
        List<Blog> blogList = blogMapper.findBlogList(pageUtil);
        List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
        int total = blogMapper.getTotalBlogs(pageUtil); //总的数量
        PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());

        return pageResult;
    }




    /**
     * 数据填充  把BlogListVOs  By  Blogs  填充
     */
    private  List <BlogListVO> getBlogListVOsByBlogs(List<Blog> blogList){

        ArrayList<BlogListVO> blogListVOS = new ArrayList<>();
        //验证非空
        if (!CollectionUtils.isEmpty(blogList)){
            //获取到集合的目录的ID  ?????????????????????????????
            List<Integer> catagoryIds = blogList.stream().map(Blog::getBlogCategoryId).collect(Collectors.toList());

            Map<Integer, String> blogCategoryMap = new HashMap<>();

            //验证集合非空
            //list  Map 的存储方式
            if (!CollectionUtils.isEmpty(catagoryIds)){
                List<BlogCategory> blogCategories = categoryMapper.selectByCategoryIds(catagoryIds);
                //验证非空
                if (!CollectionUtils.isEmpty(blogCategories)){
                    //blog的目录集合  可以把放在Map 里面形成key , Value
                    blogCategories.stream().collect(Collectors.toMap(BlogCategory::getCategoryId, BlogCategory::getCategoryIcon, (key1, key2) -> key2));
                }

            }
            //遍历 博客
            for (Blog blog : blogList) {
                BlogListVO blogListVO = new BlogListVO();
                //这里是把数据复制过去
                BeanUtils.copyProperties(blog,blogListVO);
                if (blogCategoryMap.containsKey(blog.getBlogCategoryId())){
                    blogListVO.setBlogCategoryIcon(blogCategoryMap.get(blog.getBlogCategoryId()));
                }else{
                    blogListVO.setBlogCategoryId(0);
                    blogListVO.setBlogCategoryName("默认分类");
                    blogListVO.setBlogCategoryIcon("/admin/dist/img/category/00.png");
                }
                blogListVOS.add(blogListVO);

            }
        }

        return blogListVOS;

    }


    /**
     * 根据搜索关键字获取首页文章的列表
     *
     * 分页的流程  创建一个Map ,放入到工具类里面 ，
     * 然后查询到bloglist 集合， 转换成VOS 集合，然后 获取到数据量 ，
     * 结果进行分页处理
     * 返回数据
     */
    public PageResult getBlogsPageBySearch(String keyword,int page){
        //验证参数的 page >0  和 关键字的验证
        if (page > 0 && PatternUtil.validKeyword(keyword)){

            Map param = new HashMap();
            param.put("page",page);
            param.put("limit",9);
            param.put("keyword",keyword);
            param.put("blogStatus",1);//过滤发布的数据

            PageQueryUtil pageUtil = new PageQueryUtil(param);
            //博客通过数据查询到blogList
            List<Blog> blogList = blogMapper.findBlogList(pageUtil);
            List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
            int total = blogMapper.getTotalBlogs(pageUtil);
            PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
            return pageResult;

        }
        return null;
    }

    /**
     * 根据分类获取到 文章的列表
     * @param categoryName
     * @param page
     * @return
     */
    @Override
    public PageResult getBlogsPageByCategory(String categoryName, int page) {
        if (PatternUtil.validKeyword(categoryName)){
            BlogCategory blogCategory = categoryMapper.selectByCategoryName(categoryName);
            if ("默认分类".equals(categoryName) && blogCategory ==null){
                blogCategory = new BlogCategory();
                blogCategory.setCategoryId(0); //默认分类 设置目录id 为0
            }
            //目录 不等于 空 页数大于0
            if (blogCategory!=null && page >0){
                Map param=new HashMap();
                param.put("page",page);
                param.put("limit",9);
                param.put("blogCategoryId",blogCategory.getCategoryId());
                param.put("blogStatus",1);//
                PageQueryUtil pageUtil = new PageQueryUtil(param);
                List<Blog> blogList = blogMapper.findBlogList(pageUtil);
                List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
                int total = blogMapper.getTotalBlogs(pageUtil);
                PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
                return pageResult;

            }

        }
        return null;
    }

    /**
     * * 根据标签获取首页文章列表
     * @param tagName
     * @param page
     * @return
     */
    @Override
    public PageResult getBlogsPageByTag(String tagName, int page) {
        if (PatternUtil.validKeyword(tagName)){
            BlogTag tag = tagMapper.selectByTagName(tagName);
            if (tag!=null){
                Map param = new HashMap();
                param.put("page",page);
                param.put("limit",9);
                param.put("tagId",tag.getTagId());
                PageQueryUtil pageUtil = new PageQueryUtil(param);
                List<Blog> bloglist = blogMapper.getBlogsPageByTagId(pageUtil);
                //转换成返回的list
                List<BlogListVO> blogListVOsByBlogs = getBlogListVOsByBlogs(bloglist);
                int total = blogMapper.getTotalBlogs(pageUtil);
                PageResult pageResult = new PageResult(blogListVOsByBlogs, total, pageUtil.getLimit(), pageUtil.getPage());
                return pageResult;
            }

        }
        return null;
    }

    /**
     * 文章详情页的获取
     * @param blogId
     * @return
     */
    @Override
    public BlogDetailVO getBlogDetail(Long blogId) {
        //通过blogID 查询 获取到博客
        Blog blog = blogMapper.selectByPrimaryKey(blogId);
        //把 blog 的数据 复制到 返回数据中 BlogDetailVO
        //不为空且状态已发布   把博客放到方法的中  抽取的一个方法中
        BlogDetailVO blogDetailVO=getBlogDetailVO(blog);

        //验证复制的 blogDetailVO 是否为空
        if (blogDetailVO !=null){
            return blogDetailVO;
        }

        return null;
    }

    /**
     * 方法的抽取出来
     */
    private BlogDetailVO getBlogDetailVO(Blog blog){

        //验证blog 非空 博客的状态是已经发布的
        if (blog !=null && blog.getBlogStatus() ==1){
            //增加浏览量
            blog.setBlogViews(blog.getBlogViews() + 1 );
            //更新博客
            blogMapper.updateByPrimaryKey(blog);
            //创建对象之间的属性赋值
            BlogDetailVO blogDetailVO = new BlogDetailVO();
            //对象之间的属性赋值
            BeanUtils.copyProperties(blog,blogDetailVO);
            //md格式之间的转换  使用工具类把MD的内容转换成 HTML
            blogDetailVO.setBlogContent(MarkDownUtil.mdToHtml(blogDetailVO.getBlogContent()));

            //通过博客分类id  查询博客分类
            BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());

            //验证非空
            if (blogCategory ==null){
                //创建博客分类
                blogCategory= new BlogCategory();
                //默认分类的 0
                blogCategory.setCategoryId(0);
                //默认的图标
                blogCategory.setCategoryIcon("/admin/dist/img/category/00.png");
            }

             //分类的信息  VO设置分类的图标
            blogDetailVO.setBlogCategoryIcon(blogCategory.getCategoryIcon());
            //验证博客标签 非空
            if (!StringUtils.isEmpty(blog.getBlogTags())){

                //标签的设置    获取到标签 用 ， 分开 然后变成集合
                List<String> tags = Arrays.asList(blog.getBlogTags().split(","));
                //把返回的Tag返回信息  的标签中
                blogDetailVO.setBlogTags(tags);
            }
            return blogDetailVO;
        }
         return null;
    }

}
