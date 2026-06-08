package com.wearsky.demo.click.listener;

import com.wearsky.demo.click.domain.entity.ClickBlog;
import com.wearsky.demo.click.domain.entity.ClickReply;
import com.wearsky.demo.click.enums.Like;
import com.wearsky.demo.click.service.ClickBlogService;
import com.wearsky.demo.click.service.ClickReplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class ClickListener {

    private ClickBlogService clickBlogServiceImpl;

    private ClickReplyService clickReplyServiceImpl;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "like.blog", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "like.blog"
    ))
    public void listenClickLikeBlog(Map<String, Long> map) {
        log.debug("点赞：用户、{} 、博客{}", map.get("userId"), map.get("blogId"));
        ClickBlog clickBlog = new ClickBlog();
        clickBlog.setUserId(map.get("userId"));
        clickBlog.setBlogId(map.get("blogId"));
        clickBlog.setIsLike(Like.LIKE);
        clickBlogServiceImpl.clickLike(clickBlog);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "undo.like.blog", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "undo.like.blog"
    ))
    public void listenUndoLikeBlog(Map<String, Long> map) {
        log.debug("取消点赞：用户、{} 、博客{}", map.get("userId"), map.get("blogId"));
        ClickBlog clickBlog = new ClickBlog();
        clickBlog.setUserId(map.get("userId"));
        clickBlog.setBlogId(map.get("blogId"));
        clickBlogServiceImpl.undoLike(clickBlog);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dislike.blog", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "dislike.blog"
    ))
    public void listenClickDislikeBlog(Map<String, Long> map) {
        log.debug("点踩：用户、{} 、博客{}", map.get("userId"), map.get("blogId"));
        ClickBlog clickBlog = new ClickBlog();
        clickBlog.setUserId(map.get("userId"));
        clickBlog.setBlogId(map.get("blogId"));
        clickBlog.setIsLike(Like.DISLIKE);
        clickBlogServiceImpl.clickDislike(clickBlog);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "undo.dislike.blog", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "undo.dislike.blog"
    ))
    public void listenUndoDislikeBlog(Map<String, Long> map) {
        log.debug("取消点踩：用户、{} 、博客{}", map.get("userId"), map.get("blogId"));
        ClickBlog clickBlog = new ClickBlog();
        clickBlog.setUserId(map.get("userId"));
        clickBlog.setBlogId(map.get("blogId"));
        clickBlogServiceImpl.undoDislike(clickBlog);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "like.reply", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "like.reply"
    ))
    public void listenClickLikeReply(Map<String, Long> map) {
        log.debug("点赞：用户、{} 、评论{}", map.get("userId"), map.get("replyId"));
        ClickReply clickReply = new ClickReply();
        clickReply.setUserId(map.get("userId"));
        clickReply.setReplyId(map.get("replyId"));
        clickReply.setIsLike(Like.LIKE);
        clickReplyServiceImpl.clickLike(clickReply);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "undo.like.reply", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "undo.like.reply"
    ))
    public void listenUndoLikeReply(Map<String, Long> map) {
        log.debug("取消点赞：用户、{} 、评论{}", map.get("userId"), map.get("replyId"));
        ClickReply clickReply = new ClickReply();
        clickReply.setUserId(map.get("userId"));
        clickReply.setReplyId(map.get("replyId"));
        clickReplyServiceImpl.undoLike(clickReply);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dislike.reply", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "dislike.reply"
    ))
    public void listenClickDislikeReply(Map<String, Long> map) {
        log.debug("点踩：用户、{} 、评论{}", map.get("userId"), map.get("replyId"));
        ClickReply clickReply = new ClickReply();
        clickReply.setUserId(map.get("userId"));
        clickReply.setReplyId(map.get("replyId"));
        clickReply.setIsLike(Like.DISLIKE);
        clickReplyServiceImpl.clickDislike(clickReply);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "undo.dislike.reply", durable = "true"),
            exchange = @Exchange(name = "click"),
            key = "undo.dislike.reply"
    ))
    public void listenUndoDislikeReply(Map<String, Long> map) {
        log.debug("取消点踩：用户、{} 、评论{}", map.get("userId"), map.get("replyId"));
        ClickReply clickReply = new ClickReply();
        clickReply.setUserId(map.get("userId"));
        clickReply.setReplyId(map.get("replyId"));
        clickReplyServiceImpl.undoDislike(clickReply);
    }
}
