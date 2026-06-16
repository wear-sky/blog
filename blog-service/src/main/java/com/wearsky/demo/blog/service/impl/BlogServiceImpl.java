package com.wearsky.demo.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.wearsky.demo.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

        List<Blog> blogs = blogPage.getRecords();
        List<BlogVO> blogVOs = BeanUtil.copyToList(blogs, BlogVO.class);

        // 批量获取作者信息
        if (!blogs.isEmpty()) {
            List<Long> authorIds = blogs.stream()
                    .map(Blog::getAuthorId)
                    .distinct()
                    .toList();

            ApiResponse<List<UserVO>> response = userClient.getUserByIds(authorIds);
            if (response.getData() != null) {
                Map<Long, UserVO> authorMap = response.getData().stream()
                        .collect(Collectors.toMap(UserVO::getId, user -> user));

                blogVOs.forEach(blogVO -> blogVO.setAuthor(authorMap.get(blogVO.getAuthorId())));
            }
        }

        BlogPageVO vo = new BlogPageVO();
        vo.setTotal(blogPage.getTotal());
        vo.setBlogs(blogVOs);
        return vo;
    }

    @Override
    public void likeBlog(Long blogId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("blogId", blogId);
        // 1.创建CorrelationData
        CorrelationData cd = new CorrelationData();
        // 2.给Future设置whenComplete
        cd.getFuture().toCompletableFuture().whenComplete((result, throwable) -> {
            if (throwable != null) {
                // 2.1.Future发生异常时的处理逻辑，基本不会触发
                log.error("send message fail", throwable);
            }
            if (result != null) {
                // 2.2.Future接收到回执的处理逻辑，参数中的result就是回执内容
                if (result.isAck()) { // result.isAck()，boolean类型，true代表ack回执，false 代表 nack回执
                    log.debug("发送消息成功，收到 ack!");
                } else { // result.getReason()，String类型，返回nack时的异常描述
                    log.error("发送消息失败，收到 nack, reason : {}", result.getReason());
                }
            }
        });
        rabbitTemplate.convertAndSend("click", "like.blog", map, cd);
    }

    @Override
    public void undoLikeBlog(Long blogId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("blogId", blogId);
        rabbitTemplate.convertAndSend("click", "undo.like.blog", map);
    }

    @Override
    public void dislikeBlog(Long blogId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("blogId", blogId);
        rabbitTemplate.convertAndSend("click", "dislike.blog", map);
    }

    @Override
    public void undoDislikeBlog(Long blogId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("blogId", blogId);
        rabbitTemplate.convertAndSend("click", "undo.dislike.blog", map);
    }

    @Override
    @Transactional
    public Boolean deleteBlogByAuthorId(Long authorId) {
        List<Long> ids = lambdaQuery().eq(Blog::getAuthorId, authorId).list().stream().map(Blog::getId).toList();
        return removeByIds(ids);
    }
}
