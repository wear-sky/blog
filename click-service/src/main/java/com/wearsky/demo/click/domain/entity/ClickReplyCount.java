package com.wearsky.demo.click.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ClickReplyCount {

    private Long replyId;

    @TableField(value = "COUNT(*)")
    private Long count;

}
