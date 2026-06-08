package com.wearsky.demo.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wearsky.demo.blog.domain.dto.CreateReplyDTO;
import com.wearsky.demo.blog.domain.entity.Reply;
import com.wearsky.demo.blog.domain.vo.ReplyTreeVO;

import java.util.List;

public interface IReplyService extends IService<Reply> {

    Long createReply(CreateReplyDTO dto, Long userId);

    Boolean deleteReply(Long id, Long userId);

    List<ReplyTreeVO> getReplyTree(Long blogId);

    void likeReply(Long replyId, Long userId);

    void undoLikeReply(Long replyId, Long userId);

    void dislikeReply(Long replyId, Long userId);

    void undoDislikeReply(Long replyId, Long userId);
}
