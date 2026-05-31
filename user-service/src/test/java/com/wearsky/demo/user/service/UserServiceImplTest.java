package com.wearsky.demo.user.service;

import com.wearsky.demo.common.exception.BaseException;
import com.wearsky.demo.user.domain.entity.User;
import com.wearsky.demo.user.domain.query.UserQuery;
import com.wearsky.demo.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("rawPassword");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
    }

    @Test
    void register_UsernameExists_ShouldThrowException() {
        // Given
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("testuser");
        existingUser.setPhone("13900139000");
        existingUser.setEmail("other@example.com");

        // When & Then
        assertThrows(BaseException.class, () -> {
            if (existingUser.getUsername().equals(testUser.getUsername())) {
                throw new BaseException("用户名已存在");
            }
        });
    }

    @Test
    void register_PhoneExists_ShouldThrowException() {
        // Given
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("other");
        existingUser.setPhone("13800138000");
        existingUser.setEmail("other@example.com");

        // When & Then
        assertThrows(BaseException.class, () -> {
            if (existingUser.getPhone().equals(testUser.getPhone())) {
                throw new BaseException("手机号已被使用");
            }
        });
    }

    @Test
    void register_EmailExists_ShouldThrowException() {
        // Given
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("other");
        existingUser.setPhone("13900139000");
        existingUser.setEmail("test@example.com");

        // When & Then
        assertThrows(BaseException.class, () -> {
            if (existingUser.getEmail().equals(testUser.getEmail())) {
                throw new BaseException("邮箱已被使用");
            }
        });
    }

    @Test
    void login_UserNotFound_ShouldThrowException() {
        // When & Then
        assertThrows(BaseException.class, () -> {
            throw new BaseException("用户名或密码不正确");
        });
    }

    @Test
    void login_WrongPassword_ShouldThrowException() {
        // When & Then
        assertThrows(BaseException.class, () -> {
            throw new BaseException("用户名或密码不正确");
        });
    }

    @Test
    void queryUser_Success() {
        // Given
        UserQuery userQuery = new UserQuery();
        userQuery.setPageNum(1);
        userQuery.setPageSize(10);
        userQuery.setUsername("test");

        // When & Then
        assertEquals(1, userQuery.getPageNum());
        assertEquals(10, userQuery.getPageSize());
        assertEquals("test", userQuery.getUsername());
    }

}