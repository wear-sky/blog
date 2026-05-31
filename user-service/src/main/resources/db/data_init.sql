INSERT INTO user_service.role (id, name, code, description) VALUES (1, '管理员', 'ADMIN', '管理员角色');
INSERT INTO user_service.role (id, name, code, description) VALUES (2, '用户', 'USER', '普通用户');

INSERT INTO user_service.permission (id, name, code, description) VALUES (1, '用户读取', 'user:read', '读取用户信息');
INSERT INTO user_service.permission (id, name, code, description) VALUES (2, '用户创建', 'user:create', '用户注册');
INSERT INTO user_service.permission (id, name, code, description) VALUES (3, '用户更新', 'user:update', '更新用户信息');
INSERT INTO user_service.permission (id, name, code, description) VALUES (4, '用户删除', 'user:delete', '删除用户');
INSERT INTO user_service.permission (id, name, code, description) VALUES (5, '角色读取', 'role:read', '读取角色信息');
INSERT INTO user_service.permission (id, name, code, description) VALUES (6, '角色管理', 'role:manage', '管理角色信息');
INSERT INTO user_service.permission (id, name, code, description) VALUES (7, '权限读取', 'permission:read', '读取权限信息');
INSERT INTO user_service.permission (id, name, code, description) VALUES (8, '权限管理', 'permission:manage', '管理权限信息');

INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (1, 1, 1);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (2, 1, 2);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (3, 1, 3);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (4, 1, 4);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (5, 1, 5);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (6, 1, 6);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (7, 1, 7);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (8, 1, 8);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (9, 2, 1);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (10, 2, 2);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (11, 2, 3);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (12, 2, 4);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (13, 2, 5);
INSERT INTO user_service.role_permission (id, role_id, permission_id) VALUES (14, 2, 7);
