package com.wearsky.demo.blog.service;

import com.wearsky.demo.blog.domain.dto.CreateReplyDTO;
import com.wearsky.demo.blog.mapper.ReplyMapper;
import com.wearsky.demo.blog.service.impl.ReplyServiceImpl;
import com.wearsky.demo.common.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyServiceImplTest {

    @InjectMocks
    private ReplyServiceImpl replyService;

    @Mock
    private ReplyMapper replyMapper;

    @Mock
    private IBlogService blogService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(replyService, "baseMapper", replyMapper);
    }

    @Test
    void createReply_BlogNotFound_ShouldThrow() {
        when(blogService.getById(1L)).thenReturn(null);
        CreateReplyDTO dto = new CreateReplyDTO();
        dto.setBlogId(1L);
        dto.setContent("内容");
        assertThrows(BaseException.class, () -> replyService.createReply(dto, 1L));
    }
}
