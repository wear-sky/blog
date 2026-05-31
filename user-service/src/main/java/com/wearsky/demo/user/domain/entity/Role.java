package com.wearsky.demo.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Role {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
