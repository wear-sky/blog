package com.wearsky.demo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.domain.query.RoleQuery;
import com.wearsky.demo.user.domain.vo.RolePageVO;
import com.wearsky.demo.user.service.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private IRoleService roleService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        roleService = mock(IRoleService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RoleController(roleService)).build();
    }

    @Test
    void queryRoles_Success() throws Exception {
        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setName("管理员");
        testRole.setCode("ADMIN");
        testRole.setDescription("系统管理员");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());

        RolePageVO pageVO = new RolePageVO();
        pageVO.setTotal(1L);
        pageVO.setRoles(List.of(testRole));

        when(roleService.queryRole(any(RoleQuery.class))).thenReturn(pageVO);

        mockMvc.perform(get("/user-service/role/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void getRole_Success() throws Exception {
        Role testRole = new Role();
        testRole.setId(1L);
        testRole.setName("管理员");
        testRole.setCode("ADMIN");

        when(roleService.getById(1L)).thenReturn(testRole);

        mockMvc.perform(get("/user-service/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("管理员"));
    }

    @Test
    void createRole_Success() throws Exception {
        Role newRole = new Role();
        newRole.setName("普通用户");
        newRole.setCode("USER");
        newRole.setDescription("普通用户");

        when(roleService.save(any(Role.class))).thenReturn(true);

        mockMvc.perform(post("/user-service/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateRole_Success() throws Exception {
        Role updateRole = new Role();
        updateRole.setId(1L);
        updateRole.setName("超级管理员");
        updateRole.setCode("ADMIN");

        when(roleService.updateById(any(Role.class))).thenReturn(true);

        mockMvc.perform(put("/user-service/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteRole_Success() throws Exception {
        when(roleService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/user-service/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
