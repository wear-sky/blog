package com.wearsky.demo.blog.controller;

import com.wearsky.demo.blog.domain.dto.CreateBlogDTO;
import com.wearsky.demo.blog.domain.dto.UpdateBlogDTO;
import com.wearsky.demo.blog.domain.query.BlogQuery;
import com.wearsky.demo.blog.domain.vo.BlogPageVO;
import com.wearsky.demo.blog.domain.vo.BlogVO;
import com.wearsky.demo.blog.service.IBlogService;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "博客模块")
@RestController
@RequestMapping("/blog-service/blog")
@AllArgsConstructor
public class BlogController {

    private final IBlogService blogService;

    @Operation(summary = "发布博客")
    @PostMapping
    public ApiResponse<Long> createBlog(@RequestBody @Valid CreateBlogDTO dto) {
        Long authorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.success(blogService.createBlog(dto, authorId));
    }

    @Operation(summary = "更新博客")
    @PutMapping
    public ApiResponse<Void> updateBlog(@RequestBody @Valid UpdateBlogDTO dto) {
        Long authorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return blogService.updateBlog(dto, authorId) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "更新失败");
    }

    @Operation(summary = "删除博客")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBlog(@PathVariable Long id) {
        Long authorId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return blogService.deleteBlog(id, authorId) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
    }

    @Operation(summary = "获取博客详情")
    @GetMapping("/{id}")
    public ApiResponse<BlogVO> getBlog(@PathVariable Long id) {
        return ApiResponse.success(blogService.getBlog(id));
    }

    @Operation(summary = "查询博客列表")
    @GetMapping("/query")
    public ApiResponse<BlogPageVO> queryBlogs(BlogQuery query) {
        return ApiResponse.success(blogService.queryBlogs(query));
    }

    @Operation(summary = "给博客点赞")
    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        blogService.likeBlog(id, userId);
        return ApiResponse.success();
    }

    @Operation(summary = "取消博客点赞")
    @PostMapping("/{id}/undoLike")
    public ApiResponse<Void> undoLike(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        blogService.undoLikeBlog(id, userId);
        return ApiResponse.success();
    }

    @Operation(summary = "给博客点踩")
    @PostMapping("/{id}/dislike")
    public ApiResponse<Void> dislike(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        blogService.dislikeBlog(id, userId);
        return ApiResponse.success();
    }

    @Operation(summary = "取消博客点踩")
    @PostMapping("/{id}/undoDislike")
    public ApiResponse<Void> undoDislike(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        blogService.undoDislikeBlog(id, userId);
        return ApiResponse.success();
    }

    @Operation(summary = "删除作者的所有博客")
    @DeleteMapping("/author/{authorId}")
    public ApiResponse<Void> deleteBogsByAuthorId(@PathVariable Long authorId) {
        return blogService.deleteBlogByAuthorId(authorId)
                ? ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
    }
}
