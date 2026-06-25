package com.wearsky.demo.click.controller;

import com.wearsky.demo.click.service.ClickBlogService;
import com.wearsky.demo.click.service.ClickReplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClickControllerTest {

    private MockMvc mockMvc;
    private ClickBlogService clickBlogService;

    @BeforeEach
    void setUp() {
        clickBlogService = mock(ClickBlogService.class);
        ClickReplyService clickReplyService = mock(ClickReplyService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ClickController(clickBlogService, clickReplyService)).build();
    }

    @Test
    void getClickCount4LikeBlog() throws Exception {
        when(clickBlogService.getClickCount4LikeBlog(1L)).thenReturn(10L);
        mockMvc.perform(get("/click-service/click/like/blog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void getClickCount4DislikeBlog() throws Exception {
        when(clickBlogService.getClickCount4DislikeBlog(1L)).thenReturn(3L);
        mockMvc.perform(get("/click-service/click/dislike/blog/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(3));
    }
}
