package com.wearsky.demo.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 全文搜索 MQ 消息体
 */
@Data
public class SearchContentDTO implements Serializable {

    /**
     * 博客或回复的 ID
     */
    private Long id;

    /**
     * 类型："blog" 或 "reply"
     */
    private String type;

    /**
     * 回复所属博客 ID（博客自身为 null）
     */
    private Long blogId;

    /**
     * 博客标题（回复为 null）
     */
    private String title;

    /**
     * 正文内容
     */
    private String content;

    /**
     * 作者/回复者 ID
     */
    private Long authorId;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 批量删除时的 ID 列表
     */
    private List<Long> ids;
}
