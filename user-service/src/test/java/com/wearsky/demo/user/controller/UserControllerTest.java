package com.wearsky.demo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.domain.vo.UserVO;
import com.wearsky.demo.user.domain.dto.LoginDTO;
import com.wearsky.demo.user.domain.dto.RegisterDTO;
import com.wearsky.demo.user.domain.dto.UpdateUserDTO;
import com.wearsky.demo.user.domain.entity.User;
import com.wearsky.demo.user.domain.query.UserQuery;
import com.wearsky.demo.user.domain.vo.UserPageVO;
import com.wearsky.demo.user.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userServiceImpl;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private UserVO testUserVO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");

        testUserVO = new UserVO();
        testUserVO.setId(1L);
        testUserVO.setUsername("testuser");
        testUserVO.setPhone("13800138000");
        testUserVO.setEmail("test@example.com");
        testUserVO.setNickname("Test User");
    }

    @Test
    void register_Success() throws Exception {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password123");
        registerDTO.setPhone("13900139000");
        registerDTO.setEmail("new@example.com");

        when(userServiceImpl.register(any(User.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/user-service/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"));
    }

    @Test
    void register_Failure() throws Exception {
        // Given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("existinguser");
        registerDTO.setPassword("password123");
        registerDTO.setPhone("13900139000");
        registerDTO.setEmail("existing@example.com");

        when(userServiceImpl.register(any(User.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/user-service/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注册失败"));
    }

    @Test
    void login_Success() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        when(userServiceImpl.login(loginDTO)).thenReturn("jwt-token");

        // When & Then
        mockMvc.perform(post("/user-service/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("jwt-token"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void queryUsers_WithAdminRole_Success() throws Exception {
        // Given
        UserPageVO pageVO = new UserPageVO();
        pageVO.setTotal(1L);
        pageVO.setUsers(Collections.singletonList(testUserVO));

        when(userServiceImpl.queryUser(any(UserQuery.class))).thenReturn(pageVO);

        // When & Then
        mockMvc.perform(get("/user-service/user/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void queryUsers_WithUserRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(get("/user-service/user/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_Success() throws Exception {
        // Given
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setNickname("Updated Nickname");

        when(userServiceImpl.updateById(any(User.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/user-service/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO))
                        .with(csrf())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                        1L, null, java.util.Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        // Given
        when(userServiceImpl.removeById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/user-service/user/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_NotFound() throws Exception {
        // Given
        when(userServiceImpl.removeById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/user-service/user/999")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }

    @Test
    void me_Success() throws Exception {
        // Given
        when(userServiceImpl.getById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/user-service/user/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                        1L, null, java.util.Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
}
