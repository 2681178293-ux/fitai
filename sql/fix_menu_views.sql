-- 修复「内容与课程」「系统管理」菜单 path/component 与前端 views 不一致
-- 库：fitai_all

-- ========== 内容与课程 ==========
UPDATE sys_menu
SET path = '/media/library',
    component = '/media/library/index',
    route_name = 'MediaLibrary'
WHERE menu_id = 24;

UPDATE sys_menu
SET path = '/course/list',
    component = '/course/list/index',
    route_name = 'CourseList'
WHERE menu_id = 22;

UPDATE sys_menu
SET path = '/community/moderation',
    component = '/community/moderation/index',
    route_name = 'CommunityModeration'
WHERE menu_id = 26;

UPDATE sys_menu
SET path = '/community/post',
    component = '/community/post/index',
    route_name = 'CommunityPost'
WHERE menu_id = 25;

UPDATE sys_menu
SET path = '/course/progress',
    component = '/course/progress/index',
    route_name = 'CourseProgress'
WHERE menu_id = 27;

UPDATE sys_menu
SET path = '/community/comment',
    component = '/community/comment/index',
    route_name = 'CommunityComment'
WHERE menu_id = 28;

UPDATE sys_menu
SET path = '/course/video',
    component = '/course/video/index',
    route_name = 'CourseVideo'
WHERE menu_id = 23;

UPDATE sys_menu
SET path = '/course/category',
    component = '/course/category/index',
    route_name = 'CourseCategory'
WHERE menu_id = 21;

-- ========== 系统管理 ==========
UPDATE sys_menu
SET path = '/system/account',
    component = '/system/account/index',
    route_name = 'SystemAccount'
WHERE menu_id = 101;

UPDATE sys_menu
SET path = '/system/role',
    component = '/system/role/index',
    route_name = 'SystemRole'
WHERE menu_id = 102;

UPDATE sys_menu
SET path = '/system/menu',
    component = '/system/menu/index',
    route_name = 'SystemMenu'
WHERE menu_id = 103;

UPDATE sys_menu
SET path = '/system/department',
    component = '/system/department/index',
    route_name = 'SystemDepartment'
WHERE menu_id = 104;

UPDATE sys_menu
SET path = '/system/dict',
    component = '/system/dict/index',
    route_name = 'SystemDict'
WHERE menu_id = 105;

UPDATE sys_menu
SET path = '/system/task',
    component = '/system/task/index',
    route_name = 'SystemTask'
WHERE menu_id = 107;

UPDATE sys_menu
SET path = '/system/config',
    component = '/system/config/index',
    route_name = 'SystemConfig'
WHERE menu_id = 108;

UPDATE sys_menu
SET path = '/system/notice',
    component = '/system/notice/index',
    route_name = 'SystemNotice'
WHERE menu_id = 109;

-- 登录/操作日志共用 system/log 页面（路径保持不同，避免路由冲突）
UPDATE sys_menu
SET path = '/system/login-log',
    component = '/system/log/index',
    route_name = 'SystemLoginLog'
WHERE menu_id = 1061;

UPDATE sys_menu
SET path = '/system/oper-log',
    component = '/system/log/index',
    route_name = 'SystemOperLog'
WHERE menu_id = 1062;

-- ========== 商城管理 ==========
UPDATE sys_menu
SET path = '/product/list',
    component = '/product/list/index',
    route_name = 'productList'
WHERE menu_id = 73;
