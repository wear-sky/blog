package com.wearsky.demo.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wearsky.demo.common.exception.BaseException;
import com.wearsky.demo.blog.domain.dto.CreateBlogDTO;
import com.wearsky.demo.blog.domain.dto.UpdateBlogDTO;
import com.wearsky.demo.blog.domain.entity.Blog;
import com.wearsky.demo.blog.domain.query.BlogQuery;
import com.wearsky.demo.blog.domain.vo.BlogPageVO;
import com.wearsky.demo.blog.domain.vo.BlogVO;
import com.wearsky.demo.blog.mapper.BlogMapper;
import com.wearsky.demo.blog.service.IBlogService;
import com.wearsky.demo.common.client.UserClient;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.common.domain.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    private final UserClient userClient;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBlog(CreateBlogDTO dto, Long authorId) {
        Blog blog = BeanUtil.toBean(dto, Blog.class);
        blog.setAuthorId(authorId);
        this.save(blog);
        return blog.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBlog(UpdateBlogDTO dto, Long authorId) {
        Blog blog = this.getById(dto.getId());
        if (blog == null) {
            throw new BaseException("博客不存在");
        }
        if (!blog.getAuthorId().equals(authorId)) {
            throw new BaseException("只能修改自己的博客");
        }
        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setCreatedAt(null);
        blog.setUpdatedAt(null);
        return this.updateById(blog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBlog(Long id, Long authorId) {
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new BaseException("博客不存在");
        }
        if (!blog.getAuthorId().equals(authorId)) {
            throw new BaseException("只能删除自己的博客");
        }
        return this.removeById(id);
    }

    @Override
    public BlogVO getBlog(Long id) {
        Blog blog = this.getById(id);
        if (blog == null) {
            throw new BaseException("博客不存在");
        }
        ApiResponse<UserVO> me = userClient.getUser(blog.getAuthorId());
        BlogVO blogVO = BeanUtil.toBean(blog, BlogVO.class);
        blogVO.setAuthor(me.getData());
        return blogVO;
    }

    @Override
    public BlogPageVO queryBlogs(BlogQuery query) {
        Page<Blog> page = new Page<>(query.getPageNum(), query.getPageSize());
        if (StrUtil.isNotBlank(query.getOrderBy()) && query.getIsAsc() != null) {
            page.addOrder(query.getIsAsc() ? OrderItem.asc(query.getOrderBy()) : OrderItem.desc(query.getOrderBy()));
        } else {
            page.addOrder(OrderItem.desc("created_at"));
        }

        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getTitle()), Blog::getTitle, query.getTitle());
        wrapper.eq(query.getAuthorId() != null, Blog::getAuthorId, query.getAuthorId());

        Page<Blog> blogPage = this.page(page, wrapper);

        BlogPageVO vo = new BlogPageVO();
        vo.setTotal(blogPage.getTotal());
        vo.setBlogs(BeanUtil.copyToList(blogPage.getRecords(), BlogVO.class));
        return vo;
    }

    @Override
    public void likeBlog(Long blogId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("blogId", blogId);
        rabbitTemplate.convertAndSend("click", "like.blog", map);
    }

    @Override
    public void dislikeBlog(Long blogId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("blogId", blogId);
        rabbitTemplate.convertAndSend("click", "dislike.blog", map);
    }
}
