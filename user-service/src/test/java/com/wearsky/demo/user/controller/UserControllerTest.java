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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private IUserService userServiceImpl;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userServiceImpl = mock(IUserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userServiceImpl, mock(org.springframework.security.crypto.password.PasswordEncoder.class))).build();
    }

    @Test
    void register_Success() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password123");
        registerDTO.setPhone("13900139000");
        registerDTO.setEmail("new@example.com");

        when(userServiceImpl.register(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/user-service/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"));
    }

    @Test
    void register_Failure() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("existinguser");
        registerDTO.setPassword("password123");
        registerDTO.setPhone("13900139000");
        registerDTO.setEmail("existing@example.com");

        when(userServiceImpl.register(any(User.class))).thenReturn(false);

        mockMvc.perform(post("/user-service/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注册失败"));
    }

    @Test
    void login_Success() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        when(userServiceImpl.login(loginDTO)).thenReturn("jwt-token");

        mockMvc.perform(post("/user-service/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("jwt-token"));
    }

    @Test
    void queryUsers_Success() throws Exception {
        UserVO testUserVO = new UserVO();
        testUserVO.setId(1L);
        testUserVO.setUsername("testuser");

        UserPageVO pageVO = new UserPageVO();
        pageVO.setTotal(1L);
        pageVO.setUsers(Collections.singletonList(testUserVO));

        when(userServiceImpl.queryUser(any(UserQuery.class))).thenReturn(pageVO);

        mockMvc.perform(get("/user-service/user/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void updateUser_Success() throws Exception {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setNickname("Updated Nickname");

        when(userServiceImpl.updateById(any(User.class))).thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(1L, null, java.util.Collections.emptyList()));

        mockMvc.perform(put("/user-service/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteUser_Success() throws Exception {
        when(userServiceImpl.deleteUserById(1L)).thenReturn(true);

        mockMvc.perform(delete("/user-service/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        when(userServiceImpl.deleteUserById(999L)).thenReturn(false);

        mockMvc.perform(delete("/user-service/user/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }

    @Test
    void getUser_Success() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");

        when(userServiceImpl.getById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/user-service/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
}
