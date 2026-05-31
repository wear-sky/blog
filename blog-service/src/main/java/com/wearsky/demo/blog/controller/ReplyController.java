package com.wearsky.demo.blog.controller;

import com.wearsky.demo.blog.domain.dto.CreateReplyDTO;
import com.wearsky.demo.blog.domain.vo.ReplyTreeVO;
import com.wearsky.demo.blog.service.IReplyService;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "回复模块")
@RestController
@RequestMapping("/blog-service/reply")
@AllArgsConstructor
public class ReplyController {

    private final IReplyService replyService;

    @Operation(summary = "发布回复")
    @PostMapping
    public ApiResponse<Long> createReply(@RequestBody @Valid CreateReplyDTO dto) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.success(replyService.createReply(dto, userId));
    }

    @Operation(summary = "删除回复")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReply(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return replyService.deleteReply(id, userId) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
    }

    @Operation(summary = "获取博客回复树")
    @GetMapping("/tree/{blogId}")
    public ApiResponse<List<ReplyTreeVO>> getReplyTree(@PathVariable Long blogId) {
        return ApiResponse.success(replyService.getReplyTree(blogId));
    }

    @Operation(summary = "给评论点赞")
    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        replyService.likeReply(id, userId);
        return ApiResponse.success();
    }

    @Operation(summary = "给评论点踩")
    @PostMapping("/{id}/dislike")
    public ApiResponse<Void> dislike(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        replyService.dislikeReply(id, userId);
        return ApiResponse.success();
    }
}
