-- 博客点赞/踩表
CREATE TABLE click_blog (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    blog_id BIGINT NOT NULL COMMENT '博客ID',
    is_like TINYINT NOT NULL COMMENT '是否点赞（1、点赞 0、点踩）',
    UNIQUE KEY uk_blog_id_user_id (blog_id, user_id),
    INDEX idx_user_id (user_id)
) COMMENT = '博客点赞/踩表';

-- 回复点赞/踩表
CREATE TABLE click_reply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    reply_id BIGINT NOT NULL COMMENT '评论ID',
    is_like TINYINT NOT NULL COMMENT '是否点赞（1、点赞 0、点踩）',
    UNIQUE KEY uk_reply_id_user_id (reply_id, user_id),
    INDEX idx_user_id (user_id)
) COMMENT = '评论点赞/踩表';