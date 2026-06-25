package com.wearsky.demo.click.listener;

import com.wearsky.demo.click.service.ClickBlogService;
import com.wearsky.demo.click.service.ClickReplyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClickListenerTest {

    @InjectMocks
    private ClickListener clickListener;

    @Mock
    private ClickBlogService clickBlogServiceImpl;

    @Mock
    private ClickReplyService clickReplyServiceImpl;

    @Test
    void listenClickLikeBlog() {
        clickListener.listenClickLikeBlog(Map.of("userId", 1L, "blogId", 1L));
        verify(clickBlogServiceImpl).clickLike(any());
    }

    @Test
    void listenUndoLikeBlog() {
        clickListener.listenUndoLikeBlog(Map.of("userId", 1L, "blogId", 1L));
        verify(clickBlogServiceImpl).undoLike(any());
    }

    @Test
    void listenClickDislikeBlog() {
        clickListener.listenClickDislikeBlog(Map.of("userId", 1L, "blogId", 1L));
        verify(clickBlogServiceImpl).clickDislike(any());
    }

    @Test
    void listenUndoDislikeBlog() {
        clickListener.listenUndoDislikeBlog(Map.of("userId", 1L, "blogId", 1L));
        verify(clickBlogServiceImpl).undoDislike(any());
    }

    @Test
    void listenClickLikeReply() {
        clickListener.listenClickLikeReply(Map.of("userId", 1L, "replyId", 1L));
        verify(clickReplyServiceImpl).clickLike(any());
    }

    @Test
    void listenUndoLikeReply() {
        clickListener.listenUndoLikeReply(Map.of("userId", 1L, "replyId", 1L));
        verify(clickReplyServiceImpl).undoLike(any());
    }

    @Test
    void listenClickDislikeReply() {
        clickListener.listenClickDislikeReply(Map.of("userId", 1L, "replyId", 1L));
        verify(clickReplyServiceImpl).clickDislike(any());
    }

    @Test
    void listenUndoDislikeReply() {
        clickListener.listenUndoDislikeReply(Map.of("userId", 1L, "replyId", 1L));
        verify(clickReplyServiceImpl).undoDislike(any());
    }
}
