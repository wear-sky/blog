-- 权限表
CREATE TABLE permission (
	id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
	name VARCHAR(50) NOT NULL COMMENT '权限名称',
	code VARCHAR(100) NOT NULL COMMENT '权限编码',
	description VARCHAR(200) COMMENT '权限描述',
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_code (code)
) COMMENT = '权限表';

-- 角色表
CREATE TABLE role (
	id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
	name VARCHAR(50) NOT NULL COMMENT '角色名称',
	code VARCHAR(50) NOT NULL COMMENT '角色编码',
	description VARCHAR(200) COMMENT '角色描述',
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_code (code)
) COMMENT = '角色表';

-- 角色权限关系表
CREATE TABLE role_permission (
	id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
	role_id BIGINT NOT NULL COMMENT '角色ID',
	permission_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_id_permission_id (role_id, permission_id),
    FOREIGN KEY permission_id_fk (permission_id) REFERENCES permission (id),
    FOREIGN KEY role_id_fk (role_id) REFERENCES role (id)
) COMMENT = '角色权限关系表';

-- 用户表
CREATE TABLE user (
	id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
	username VARCHAR(50) NOT NULL COMMENT '用户名',
	password VARCHAR(100) NOT NULL COMMENT '密码，BCrypt加密',
	nickname VARCHAR(100) COMMENT '昵称',
	email VARCHAR(100) COMMENT '邮箱',
	phone VARCHAR(20)  COMMENT '手机号',
	avatar VARCHAR(255) COMMENT '头像',
	deleted TINYINT DEFAULT 0 NOT NULL COMMENT '逻辑删除（0=正常, 1=删除）',
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone)
) COMMENT = '用户表';

-- 用户角色关系表
CREATE TABLE user_role (
	id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
	user_id BIGINT NOT NULL COMMENT '用户ID',
	role_id BIGINT NOT NULL COMMENT '角色ID',
	UNIQUE KEY uk_user_id_role_id (user_id, role_id),
	FOREIGN KEY role_id_fk (role_id) REFERENCES role (id),
	FOREIGN KEY user_id_fk (user_id) REFERENCES user (id)
) COMMENT = '用户角色关系表';
