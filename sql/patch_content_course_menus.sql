-- 补齐「内容与课程」下老师端有、本地缺的菜单：学习进度、社区评论
-- 库：fitai_all

INSERT INTO sys_menu (menu_id, menu_name, parent_id, ancestors, order_num, path, component, route_name, menu_type, visible, status, perms, icon, del_flag, create_by, create_time, update_by, update_time)
VALUES
(27, '学习进度', 20, '0,20', 7, '/course/progress', NULL, 'CourseProgress', 'C', '0', '0', 'crs:progress:list', 'Monitor', '0', 'admin', NOW(), '', NOW()),
(28, '社区评论', 20, '0,20', 8, '/community/comment', NULL, 'CommunityComment', 'C', '0', '0', 'cms:comment:list', 'ChatDotRound', '0', 'admin', NOW(), '', NOW())
ON DUPLICATE KEY UPDATE
  menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), ancestors=VALUES(ancestors),
  order_num=VALUES(order_num), path=VALUES(path), route_name=VALUES(route_name),
  menu_type=VALUES(menu_type), visible=VALUES(visible), status=VALUES(status),
  perms=VALUES(perms), icon=VALUES(icon), del_flag='0';

INSERT INTO sys_menu (menu_id, menu_name, parent_id, ancestors, order_num, path, component, route_name, menu_type, visible, status, perms, icon, del_flag, create_by, create_time, update_by, update_time)
VALUES
(2701, '查询', 27, '0,20,27', 1, NULL, NULL, NULL, 'F', '1', '0', 'crs:progress:query', NULL, '0', 'admin', NOW(), '', NOW()),
(2702, '导出', 27, '0,20,27', 2, NULL, NULL, NULL, 'F', '1', '0', 'crs:progress:export', NULL, '0', 'admin', NOW(), '', NOW()),
(2801, '查询', 28, '0,20,28', 1, NULL, NULL, NULL, 'F', '1', '0', 'cms:comment:query', NULL, '0', 'admin', NOW(), '', NOW()),
(2802, '删除', 28, '0,20,28', 2, NULL, NULL, NULL, 'F', '1', '0', 'cms:comment:remove', NULL, '0', 'admin', NOW(), '', NOW()),
(2803, '置顶', 28, '0,20,28', 3, NULL, NULL, NULL, 'F', '1', '0', 'cms:comment:top', NULL, '0', 'admin', NOW(), '', NOW())
ON DUPLICATE KEY UPDATE del_flag='0', status='0', perms=VALUES(perms);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(2,27),(2,28),(2,2701),(2,2702),(2,2801),(2,2802),(2,2803);
