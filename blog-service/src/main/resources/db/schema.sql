-- 博客表
CREATE TABLE `blog` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    `title` VARCHAR(200) NOT NULL COMMENT '博客标题',
    `content` LONGTEXT NOT NULL COMMENT '博客内容（HTML富文本）',
    `author_id` BIGINT NOT NULL COMMENT '作者ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=正常, 1=删除）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX idx_author_id (author_id)
) COMMENT ='博客表';

-- 回复表
CREATE TABLE `reply` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    `blog_id` BIGINT NOT NULL COMMENT '所属博客ID',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父回复ID（NULL表示顶级回复）',
    `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '被回复用户ID',
    `content` TEXT NOT NULL COMMENT '回复内容（纯文本）',
    `user_id` BIGINT NOT NULL COMMENT '回复人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除（0=正常, 1=删除）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
    INDEX idx_blog_id (blog_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_user_id (user_id)
) COMMENT ='回复表';