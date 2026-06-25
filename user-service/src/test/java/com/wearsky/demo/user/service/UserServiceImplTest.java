package com.wearsky.demo.user.service;

import com.wearsky.demo.common.exception.BaseException;
import com.wearsky.demo.user.common.JwtUtil;
import com.wearsky.demo.user.domain.dto.LoginDTO;
import com.wearsky.demo.user.domain.entity.User;
import com.wearsky.demo.user.mapper.PermissionMapper;
import com.wearsky.demo.user.mapper.RoleMapper;
import com.wearsky.demo.user.mapper.UserMapper;
import com.wearsky.demo.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");

        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void login_Success() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(roleMapper.selectCodeByUserId(1L)).thenReturn(List.of("USER"));
        when(permissionMapper.selectCodeByUserId(1L)).thenReturn(List.of("blog:read"));
        when(jwtUtil.getExpirationMs()).thenReturn(3600000L);
        when(jwtUtil.generateToken(1L)).thenReturn("jwt-token");

        String result = userService.login(loginDTO);

        assertEquals("jwt-token", result);
    }

    @Test
    void login_UserNotFound_ShouldThrowException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(null);

        assertThrows(BaseException.class, () -> userService.login(loginDTO));
    }

    @Test
    void login_WrongPassword_ShouldThrowException() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("wrongpassword");

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(BaseException.class, () -> userService.login(loginDTO));
    }

    @Test
    void getAuthoritiesByUserId_WithData() {
        when(roleMapper.selectCodeByUserId(1L)).thenReturn(List.of("ADMIN"));
        when(permissionMapper.selectCodeByUserId(1L)).thenReturn(List.of("blog:create", "blog:delete"));
        when(jwtUtil.getExpirationMs()).thenReturn(3600000L);

        List<String> authorities = userService.getAuthoritiesByUserId(1L);

        assertNotNull(authorities);
        assertEquals(3, authorities.size());
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("blog:create"));
        assertTrue(authorities.contains("blog:delete"));
    }

    @Test
    void getAuthoritiesByUserId_EmptyRoles() {
        when(roleMapper.selectCodeByUserId(1L)).thenReturn(List.of());
        when(permissionMapper.selectCodeByUserId(1L)).thenReturn(List.of());
        when(jwtUtil.getExpirationMs()).thenReturn(3600000L);

        List<String> authorities = userService.getAuthoritiesByUserId(1L);

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
}
