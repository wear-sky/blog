package com.wearsky.demo.click.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wearsky.demo.click.domain.entity.ClickReply;
import com.wearsky.demo.click.domain.entity.ClickReplyCount;
import com.wearsky.demo.click.enums.Like;
import com.wearsky.demo.click.mapper.ClickReplyMapper;
import com.wearsky.demo.click.service.ClickReplyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClickReplyServiceImpl extends ServiceImpl<ClickReplyMapper, ClickReply> implements ClickReplyService {

    private final ClickReplyMapper clickReplyMapper;

    public ClickReplyServiceImpl(ClickReplyMapper clickReplyMapper) {
        this.clickReplyMapper = clickReplyMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void clickLike(ClickReply clickReply) {
        ClickReply existed = getByReplyIdAndUserId(clickReply.getReplyId(), clickReply.getUserId());
        if (existed == null) {
            this.save(clickReply);
        } else if (existed.getIsLike() == Like.DISLIKE) {
            existed.setIsLike(Like.LIKE);
            this.updateById(existed);
        }
    }

    @Override
    public void undoLike(ClickReply clickReply) {
        ClickReply existed = getByReplyIdAndUserId(clickReply.getReplyId(), clickReply.getUserId());
        if (existed != null && existed.getIsLike() == Like.LIKE) {
            removeById(existed);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void clickDislike(ClickReply clickReply) {
        ClickReply existed = getByReplyIdAndUserId(clickReply.getReplyId(), clickReply.getUserId());
        if (existed == null) {
            this.save(clickReply);
        } else if (existed.getIsLike() == Like.LIKE) {
            existed.setIsLike(Like.DISLIKE);
            this.updateById(existed);
        }
    }

    @Override
    public void undoDislike(ClickReply clickReply) {
        ClickReply existed = getByReplyIdAndUserId(clickReply.getReplyId(), clickReply.getUserId());
        if (existed != null && existed.getIsLike() == Like.DISLIKE) {
            removeById(existed);
        }
    }

    @Override
    public List<ClickReplyCount> getClickCount4LikeReplies(List<Long> replyIds) {
        return clickReplyMapper.getClickCount4LikeReplies(replyIds);
    }

    @Override
    public List<ClickReplyCount> getClickCount4DislikeReplies(List<Long> replyIds) {
        return clickReplyMapper.getClickCount4DislikeReplies(replyIds);
    }

    @Override
    public List<ClickReply> getByReplyIdsAndUserId(List<Long> replyIds, Long userId) {
        return lambdaQuery().in(ClickReply::getReplyId, replyIds).eq(ClickReply::getUserId, userId).list();
    }

    private ClickReply getByReplyIdAndUserId(Long replyId, Long userId) {
        return this.lambdaQuery().eq(ClickReply::getReplyId, replyId).eq(ClickReply::getUserId, userId).one();
    }
}
