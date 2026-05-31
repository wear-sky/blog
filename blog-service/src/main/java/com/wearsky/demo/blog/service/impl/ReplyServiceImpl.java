package com.wearsky.demo.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wearsky.demo.common.exception.BaseException;
import com.wearsky.demo.blog.domain.dto.CreateReplyDTO;
import com.wearsky.demo.blog.domain.entity.Blog;
import com.wearsky.demo.blog.domain.entity.Reply;
import com.wearsky.demo.blog.domain.vo.ReplyTreeVO;
import com.wearsky.demo.blog.mapper.ReplyMapper;
import com.wearsky.demo.blog.service.IBlogService;
import com.wearsky.demo.blog.service.IReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements IReplyService {

    private final IBlogService blogService;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReply(CreateReplyDTO dto, Long userId) {
        Blog blog = blogService.getById(dto.getBlogId());
        if (blog == null) {
            throw new BaseException("博客不存在");
        }

        // 如果是回复某条回复，验证父回复是否存在
        if (dto.getParentId() != null) {
            Reply parentReply = this.getById(dto.getParentId());
            if (parentReply == null) {
                throw new BaseException("父回复不存在");
            }
            if (!parentReply.getBlogId().equals(dto.getBlogId())) {
                throw new BaseException("父回复不属于该博客");
            }
        }

        Reply reply = BeanUtil.toBean(dto, Reply.class);
        reply.setUserId(userId);
        this.save(reply);
        return reply.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteReply(Long id, Long userId) {
        Reply reply = this.getById(id);
        if (reply == null) {
            throw new BaseException("回复不存在");
        }
        if (!reply.getUserId().equals(userId)) {
            throw new BaseException("只能删除自己的回复");
        }
        return this.removeById(id);
    }

    @Override
    public List<ReplyTreeVO> getReplyTree(Long blogId) {
        // 查询该博客的所有回复
        List<Reply> allReplies = this.list(new LambdaQueryWrapper<Reply>()
                .eq(Reply::getBlogId, blogId)
                .orderByAsc(Reply::getCreatedAt));

        if (allReplies.isEmpty()) {
            return Collections.emptyList();
        }

        // 按 parentId 分组
        Map<Long, List<Reply>> parentIdMap = allReplies.stream()
                .collect(Collectors.groupingBy(r -> r.getParentId() == null ? 0L : r.getParentId()));

        // 转换为 ReplyTreeVO
        List<ReplyTreeVO> allTreeVOs = allReplies.stream()
                .map(r -> BeanUtil.toBean(r, ReplyTreeVO.class))
                .toList();

        // 构建 Map<id, ReplyTreeVO>
        Map<Long, ReplyTreeVO> voMap = allTreeVOs.stream()
                .collect(Collectors.toMap(ReplyTreeVO::getId, r -> r));

        // 构建树
        List<ReplyTreeVO> roots = new ArrayList<>();
        for (ReplyTreeVO vo : allTreeVOs) {
            if (vo.getParentId() == null) {
                roots.add(vo);
            } else {
                ReplyTreeVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }

        return roots;
    }

    @Override
    public void likeReply(Long replyId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("replyId", replyId);
        rabbitTemplate.convertAndSend("click", "like.reply", map);
    }

    @Override
    public void dislikeReply(Long replyId, Long userId) {
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("replyId", replyId);
        rabbitTemplate.convertAndSend("click", "dislike.reply", map);
    }
}
