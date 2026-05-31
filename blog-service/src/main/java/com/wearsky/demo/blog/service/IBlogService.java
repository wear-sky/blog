package com.wearsky.demo.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wearsky.demo.blog.domain.dto.CreateBlogDTO;
import com.wearsky.demo.blog.domain.dto.UpdateBlogDTO;
import com.wearsky.demo.blog.domain.entity.Blog;
import com.wearsky.demo.blog.domain.query.BlogQuery;
import com.wearsky.demo.blog.domain.vo.BlogPageVO;
import com.wearsky.demo.blog.domain.vo.BlogVO;

public interface IBlogService extends IService<Blog> {

    Long createBlog(CreateBlogDTO dto, Long authorId);

    Boolean updateBlog(UpdateBlogDTO dto, Long authorId);

    Boolean deleteBlog(Long id, Long authorId);

    BlogVO getBlog(Long id);

    BlogPageVO queryBlogs(BlogQuery query);

    void likeBlog(Long blogId, Long userId);

    void dislikeBlog(Long blogId, Long userId);
}
