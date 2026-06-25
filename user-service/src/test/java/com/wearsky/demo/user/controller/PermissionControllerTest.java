package com.wearsky.demo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.domain.query.PermissionQuery;
import com.wearsky.demo.user.domain.vo.PermissionPageVO;
import com.wearsky.demo.user.service.IPermissionService;
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

class PermissionControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private IPermissionService permissionService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        permissionService = mock(IPermissionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PermissionController(permissionService)).build();
    }

    @Test
    void queryPermissions_Success() throws Exception {
        Permission testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setName("用户读取");
        testPermission.setCode("user:read");
        testPermission.setDescription("读取用户信息");
        testPermission.setCreatedAt(LocalDateTime.now());
        testPermission.setUpdatedAt(LocalDateTime.now());

        PermissionPageVO pageVO = new PermissionPageVO();
        pageVO.setTotal(1L);
        pageVO.setPermissions(List.of(testPermission));

        when(permissionService.queryPermission(any(PermissionQuery.class))).thenReturn(pageVO);

        mockMvc.perform(get("/user-service/permission/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void getPermission_Success() throws Exception {
        Permission testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setName("用户读取");
        testPermission.setCode("user:read");

        when(permissionService.getById(1L)).thenReturn(testPermission);

        mockMvc.perform(get("/user-service/permission/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("用户读取"));
    }

    @Test
    void createPermission_Success() throws Exception {
        Permission newPermission = new Permission();
        newPermission.setName("用户写入");
        newPermission.setCode("user:write");
        newPermission.setDescription("写入用户信息");

        when(permissionService.save(any(Permission.class))).thenReturn(true);

        mockMvc.perform(post("/user-service/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPermission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updatePermission_Success() throws Exception {
        Permission updatePermission = new Permission();
        updatePermission.setId(1L);
        updatePermission.setName("用户读取权限");
        updatePermission.setCode("user:read");

        when(permissionService.updateById(any(Permission.class))).thenReturn(true);

        mockMvc.perform(put("/user-service/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePermission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deletePermission_Success() throws Exception {
        when(permissionService.removeById(1L)).thenReturn(true);

        mockMvc.perform(delete("/user-service/permission/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
