package com.wearsky.demo.click.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wearsky.demo.click.domain.entity.ClickReply;
import com.wearsky.demo.click.domain.entity.ClickReplyCount;

import java.util.List;

public interface ClickReplyService extends IService<ClickReply> {

    void clickLike(ClickReply clickReply);

    void undoLike(ClickReply clickReply);

    void clickDislike(ClickReply clickReply);

    void undoDislike(ClickReply clickReply);

    List<ClickReplyCount> getClickCount4LikeReplies(List<Long> replyIds);

    List<ClickReplyCount> getClickCount4DislikeReplies(List<Long> replyIds);

    List<ClickReply> getByReplyIdsAndUserId(List<Long> replyIds, Long userId);
}
