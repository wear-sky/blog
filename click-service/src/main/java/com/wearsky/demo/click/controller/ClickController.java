package com.wearsky.demo.click.controller;

import cn.hutool.core.bean.BeanUtil;
import com.wearsky.demo.click.domain.vo.ClickBlog;
import com.wearsky.demo.click.domain.vo.ClickReply;
import com.wearsky.demo.click.domain.vo.ClickReplyCount;
import com.wearsky.demo.click.service.ClickBlogService;
import com.wearsky.demo.click.service.ClickReplyService;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<ClickReplyCount> clickReplyCounts = clickReplyServiceImpl.getClickCount4LikeReplies(replyIds).stream()
                .map(clickReplyCount -> BeanUtil.toBean(clickReplyCount, ClickReplyCount.class))
                .toList();
        return ApiResponse.success(clickReplyCounts);
    }

    @Operation(summary = "获取评论点踩数")
    @GetMapping("/dislike/reply")
    ApiResponse<List<ClickReplyCount>> getClickCount4DislikeReply(@RequestParam List<Long> replyIds) {
        List<ClickReplyCount> clickReplyCounts = clickReplyServiceImpl.getClickCount4DislikeReplies(replyIds).stream()
                .map((clickReplyCount) -> BeanUtil.toBean(clickReplyCount, ClickReplyCount.class))
                .toList();
        return ApiResponse.success(clickReplyCounts);
    }

    @Operation(summary = "查看当前用户对blog的点赞/踩记录")
    @GetMapping("/checkClickedBlog/{blogId}")
    ApiResponse<ClickBlog> checkClickedBlog(@PathVariable Long blogId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ClickBlog clickBlog = BeanUtil.toBean(
                clickBlogServiceImpl.getByBlogIdAndUserId(blogId, userId), ClickBlog.class);
        return ApiResponse.success(clickBlog);
    }

    @Operation(summary = "查看当前用户对评论列表的点赞/踩记录")
    @GetMapping("/checkClickedReplies/{replyIds}")
    ApiResponse<List<ClickReply>> checkClickedReplies(@PathVariable List<Long> replyIds) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ClickReply> clickReplies = clickReplyServiceImpl.getByReplyIdsAndUserId(replyIds, userId).stream()
                .map(clickReply -> BeanUtil.toBean(clickReply, ClickReply.class))
                .toList();
        return ApiResponse.success(clickReplies);
    }
}
