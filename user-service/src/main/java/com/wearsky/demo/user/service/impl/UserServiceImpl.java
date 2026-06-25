package com.wearsky.demo.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.client.BlogClient;
import com.wearsky.demo.common.domain.vo.UserVO;
import com.wearsky.demo.common.exception.BaseException;
import com.wearsky.demo.user.common.JwtUtil;
import com.wearsky.demo.user.domain.dto.LoginDTO;
import com.wearsky.demo.user.domain.entity.User;
import com.wearsky.demo.user.domain.entity.UserRole;
import com.wearsky.demo.user.domain.query.UserQuery;
import com.wearsky.demo.user.domain.vo.UserPageVO;
import com.wearsky.demo.user.enums.Roles;
import com.wearsky.demo.user.mapper.PermissionMapper;
import com.wearsky.demo.user.mapper.RoleMapper;
import com.wearsky.demo.user.mapper.UserMapper;
import com.wearsky.demo.user.mapper.UserRoleMapper;
import com.wearsky.demo.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final String AUTH_AUTHORITIES_KEY_PREFIX = "auth:authorities:";

    private final PasswordEncoder passwordEncoder;

    private final UserRoleMapper userRoleMapper;

    private final RoleMapper roleMapper;

    private final PermissionMapper permissionMapper;

    private final JwtUtil jwtUtil;

    private final BlogClient blogClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(User user) {
        query().lambda()
                .eq(User::getUsername, user.getUsername())
                .or().eq(User::getPhone, user.getPhone())
                .or().eq(User::getEmail, user.getEmail())
                .list()
                .forEach(exitUser -> {
                    if (exitUser.getUsername().equals(user.getUsername())) {
                        throw new BaseException("用户名已存在");
                    }
                    if (exitUser.getPhone().equals(user.getPhone())) {
                        throw new BaseException("手机号已被使用");
                    }
                    if (exitUser.getEmail().equals(user.getEmail())) {
                        throw new BaseException("邮箱已被使用");
                    }
                });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!save(user)) {
            return false;
        }
        int result = userRoleMapper.insert(new UserRole(user.getId(), Roles.USER.getCode()));
        return result >= 1;
    }

    @Override
    public String login(LoginDTO loginDTO) {
        User user = this.query().lambda().eq(User::getUsername, loginDTO.getUsername()).one();
        if (user == null) {
            throw new BaseException("用户名或密码不正确");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BaseException("用户名或密码不正确");
        }
        getAuthoritiesByUserId(user.getId());
        return jwtUtil.generateToken(user.getId());
    }

    @Override
    public List<String> getAuthoritiesByUserId(Long userId) {
        String redisKey = AUTH_AUTHORITIES_KEY_PREFIX + userId;

        // 1. 尝试从 Redis 获取
        try {
            String json = stringRedisTemplate.opsForValue().get(redisKey);
            if (json != null) {
                return objectMapper.readValue(json, new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            log.warn("Redis读取authorities失败, userId={}: {}", userId, e.getMessage());
        }

        // 2. Redis 无值，查库
        List<String> roleCodes = roleMapper.selectCodeByUserId(userId);
        List<String> permissionCodes = permissionMapper.selectCodeByUserId(userId);
        List<String> authorities = Stream.concat(
                roleCodes.stream().map((roleCode) -> "ROLE_" + roleCode), permissionCodes.stream()).toList();

        // 3. 写回 Redis
        try {
            stringRedisTemplate.opsForValue().set(redisKey,
                    objectMapper.writeValueAsString(authorities),
                    Duration.ofMillis(jwtUtil.getExpirationMs()));
        } catch (Exception e) {
            log.warn("写入Redis缓存authorities失败, userId={}: {}", userId, e.getMessage());
        }

        return authorities;
    }

    @Override
    public UserPageVO queryUser(UserQuery userQuery) {
        String username = userQuery.getUsername();
        String phone = userQuery.getPhone();
        String email = userQuery.getEmail();
        String nickname = userQuery.getNickname();
        LocalDateTime earliestCreatedAt = userQuery.getEarliestCreatedAt();
        LocalDateTime localDateTime = userQuery.getLastCreatedAt();
        String orderBy = userQuery.getOrderBy();
        Boolean isAsc = userQuery.getIsAsc();
        Page<User> page = new Page<>(userQuery.getPageNum(), userQuery.getPageSize());
        if (null != orderBy && null != isAsc) {
            page.addOrder(isAsc ? OrderItem.asc(orderBy) : OrderItem.desc(orderBy));
        }
        Page<User> userPage = lambdaQuery()
                .like(!StringUtils.isBlank(username), User::getUsername, username)
                .like(!StringUtils.isBlank(phone), User::getPhone, phone)
                .like(!StringUtils.isBlank(email), User::getEmail, email)
                .like(!StringUtils.isBlank(nickname), User::getNickname, nickname)
                .between(earliestCreatedAt != null && localDateTime != null, User::getCreatedAt,
                        userQuery.getEarliestCreatedAt(), userQuery.getLastCreatedAt())
                .page(page);
        UserPageVO userPageVO = new UserPageVO();
        userPageVO.setTotal(userPage.getTotal());
        userPageVO.setUsers(BeanUtil.copyToList(userPage.getRecords(), UserVO.class));
        return userPageVO;
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Boolean deleteUserById(Long id) {
        blogClient.deleteBogsByAuthorId(id);
        return removeById(id);
    }

    @Override
    public List<User> getByIds(List<Long> ids) {
        return lambdaQuery().in(User::getId, ids).list();
    }
}
