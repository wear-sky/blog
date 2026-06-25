package com.wearsky.demo.blog.service;

import com.wearsky.demo.blog.domain.dto.UpdateBlogDTO;
import com.wearsky.demo.blog.domain.entity.Blog;
import com.wearsky.demo.blog.mapper.BlogMapper;
import com.wearsky.demo.blog.service.impl.BlogServiceImpl;
import com.wearsky.demo.common.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

    @InjectMocks
    private BlogServiceImpl blogService;

    @Mock
    private BlogMapper blogMapper;

    private Blog testBlog;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(blogService, "baseMapper", blogMapper);
        testBlog = new Blog();
        testBlog.setId(1L);
        testBlog.setTitle("测试标题");
        testBlog.setContent("测试内容");
        testBlog.setAuthorId(1L);
    }

    private UpdateBlogDTO buildUpdateDTO(Long id, String title, String content) {
        UpdateBlogDTO dto = new UpdateBlogDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setContent(content);
        return dto;
    }

    @Test
    void updateBlog_Success() {
        when(blogMapper.selectById(1L)).thenReturn(testBlog);
        when(blogMapper.updateById(any(Blog.class))).thenReturn(1);
        assertTrue(blogService.updateBlog(buildUpdateDTO(1L, "新标题", "新内容"), 1L));
    }

    @Test
    void updateBlog_NotFound() {
        when(blogMapper.selectById(999L)).thenReturn(null);
        assertThrows(BaseException.class, () -> blogService.updateBlog(buildUpdateDTO(999L, "t", "c"), 1L));
    }

    @Test
    void updateBlog_NotOwner() {
        when(blogMapper.selectById(1L)).thenReturn(testBlog);
        assertThrows(BaseException.class, () -> blogService.updateBlog(buildUpdateDTO(1L, "t", "c"), 2L));
    }

    @Test
    void deleteBlog_NotFound() {
        when(blogMapper.selectById(999L)).thenReturn(null);
        assertThrows(BaseException.class, () -> blogService.deleteBlog(999L, 1L));
    }

    @Test
    void deleteBlog_NotOwner() {
        when(blogMapper.selectById(1L)).thenReturn(testBlog);
        assertThrows(BaseException.class, () -> blogService.deleteBlog(1L, 2L));
    }

    @Test
    void getBlog_NotFound() {
        when(blogMapper.selectById(999L)).thenReturn(null);
        assertThrows(BaseException.class, () -> blogService.getBlog(999L));
    }
}
