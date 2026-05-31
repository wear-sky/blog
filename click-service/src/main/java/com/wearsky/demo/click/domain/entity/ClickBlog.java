package com.wearsky.demo.click.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wearsky.demo.click.enums.Like;
import lombok.Data;

@Data

public class ClickBlog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long blogId;

    private Like isLike;

}
