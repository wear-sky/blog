package com.wearsky.demo.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long blogId;
    private Long parentId;
    private Long replyToUserId;
    private String content;
    private Long userId;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    private LocalDateTime createdAt;
}