package com.wearsky.demo.click.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wearsky.demo.click.domain.entity.ClickBlog;
import com.wearsky.demo.click.enums.Like;
import com.wearsky.demo.click.mapper.ClickBlogMapper;
import com.wearsky.demo.click.service.ClickBlogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClickBlogServiceImpl extends ServiceImpl<ClickBlogMapper, ClickBlog> implements ClickBlogService {

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void clickLike(ClickBlog clickBlog) {
        ClickBlog existed = getByBlogIdAndUserId(clickBlog.getBlogId(), clickBlog.getUserId());
        if (existed == null) {
            save(clickBlog);
        } else if (existed.getIsLike() == Like.DISLIKE) {
            existed.setIsLike(Like.LIKE);
            updateById(existed);
        }
    }

    @Override
    public void undoLike(ClickBlog clickBlog) {
        ClickBlog existed = getByBlogIdAndUserId(clickBlog.getBlogId(), clickBlog.getUserId());
        if (existed != null && existed.getIsLike() == Like.LIKE) {
            removeById(existed);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void clickDislike(ClickBlog clickBlog) {
        ClickBlog existed = getByBlogIdAndUserId(clickBlog.getBlogId(), clickBlog.getUserId());
        if (existed == null) {
            save(clickBlog);
        } else if (existed.getIsLike() == Like.LIKE) {
            existed.setIsLike(Like.DISLIKE);
            updateById(existed);
        }
    }

    @Override
    public void undoDislike(ClickBlog clickBlog) {
        ClickBlog existed = getByBlogIdAndUserId(clickBlog.getBlogId(), clickBlog.getUserId());
        if (existed != null && existed.getIsLike() == Like.DISLIKE) {
            removeById(existed);
        }
    }

    @Override
    public Long getClickCount4LikeBlog(Long blogId) {
        return lambdaQuery().eq(ClickBlog::getBlogId, blogId).eq(ClickBlog::getIsLike, Like.LIKE.getCode()).count();
    }

    @Override
    public Long getClickCount4DislikeBlog(Long blogId) {
        return lambdaQuery().eq(ClickBlog::getBlogId, blogId).eq(ClickBlog::getIsLike, Like.DISLIKE.getCode()).count();
    }

    @Override
    public ClickBlog getByBlogIdAndUserId(Long blogId, Long userId) {
        return lambdaQuery().eq(ClickBlog::getBlogId, blogId).eq(ClickBlog::getUserId, userId).one();
    }
}
