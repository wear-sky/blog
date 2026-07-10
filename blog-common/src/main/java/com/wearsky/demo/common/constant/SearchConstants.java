package com.wearsky.demo.common.constant;

/**
 * 全文搜索 RabbitMQ 常量
 */
public final class SearchConstants {

    private SearchConstants() {
    }

    /**
     * 交换机名称 (Topic 类型)
     */
    public static final String EXCHANGE = "search";

    /**
     * 队列名称
     */
    public static final String QUEUE = "search.content.queue";

    /**
     * 通配符路由键，匹配所有搜索消息
     */
    public static final String ROUTING_KEY_ALL = "search.#";

    /**
     * 博客保存（创建/更新）
     */
    public static final String BLOG_SAVE = "search.blog.save";

    /**
     * 博客删除
     */
    public static final String BLOG_DELETE = "search.blog.delete";

    /**
     * 博客批量删除
     */
    public static final String BLOG_DELETE_BATCH = "search.blog.delete.batch";

    /**
     * 回复保存（创建）
     */
    public static final String REPLY_SAVE = "search.reply.save";

    /**
     * 回复删除
     */
    public static final String REPLY_DELETE = "search.reply.delete";
}
