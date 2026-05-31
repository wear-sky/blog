package com.wearsky.demo.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.wearsky.demo.user.enums.UserDeleted;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author wearsky
 * @since 2026-05-24
 */
@Data
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    @TableLogic(value = "0", delval = "1")
    private UserDeleted deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
