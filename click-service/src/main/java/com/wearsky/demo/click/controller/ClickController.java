package com.wearsky.demo.click.controller;

import com.wearsky.demo.click.domain.entity.ClickReplyCount;
import com.wearsky.demo.click.service.ClickBlogService;
import com.wearsky.demo.click.service.ClickReplyService;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "博客点赞/踩模块")
@RestController
@RequestMapping("/click-service/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickBlogService clickBlogServiceImpl;

    private final ClickReplyService clickReplyServiceImpl;

    @Operation(summary = "获取博客点赞数")
    @GetMapping("/like/blog/{blogId}")
    ApiResponse<Long> getClickCount4LikeBlog(@PathVariable Long blogId) {
        return ApiResponse.success(clickBlogServiceImpl.getClickCount4LikeBlog(blogId));
    }

    @Operation(summary = "获取博客点踩数")
    @GetMapping("/dislike/blog/{blogId}")
    ApiResponse<Long> getClickCount4DislikeBlog(@PathVariable Long blogId) {
        return ApiResponse.success(clickBlogServiceImpl.getClickCount4DislikeBlog(blogId));
    }

    @Operation(summary = "获取评论点赞数")
    @GetMapping("/like/reply")
    ApiResponse<List<ClickReplyCount>> getClickCount4LikeReply(@RequestParam List<Long> replyIds) {
        return ApiResponse.success(clickReplyServiceImpl.getClickCount4LikeReplies(replyIds));
    }

    @Operation(summary = "获取评论点踩数")
    @GetMapping("/dislike/reply")
    ApiResponse<List<ClickReplyCount>> getClickCount4DislikeReply(@RequestParam List<Long> replyIds) {
        return ApiResponse.success(clickReplyServiceImpl.getClickCount4DislikeReplies(replyIds));
    }
}
