-- ====================================================================
-- 拼豆项目 菜单初始化脚本
-- 执行环境：freesia 数据库（与 sql/freesia.sql 同库）
-- 说明：
--   1. 页面已在 freesia-admin-layvue/src/router/module/base-routes.ts 注册静态路由，
--      即使不执行本脚本，也可通过 /fusebean/home/index 访问页面。
--   2. 本脚本用于在侧边栏/权限菜单中展示拼豆入口；执行后需为角色授权该菜单。
--   3. 菜单 ID 使用 1890000000000000xxx 段，避免与现有 ID 冲突；
--      若与已有数据冲突，请自行调整或改为自增策略。
-- ====================================================================

INSERT INTO `sys_menu`
(`ID`, `CREATOR`, `CREATE_TIME`, `MODIFIER`, `MODIFY_TIME`, `LOGIC_DEL`, `REC_VER`, `BUILD_IN`, `TENANT_ID`,
 `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY_PARAM`, `IS_FRAME`, `IS_CACHE`,
 `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`, `REMARK`)
VALUES
(1890000000000000001, 'Evad', '2026-08-26 00:00:00', 'Evad', '2026-08-26 00:00:00', 0, 1, 0, NULL,
 '拼豆项目', -1, 100, 'fusebean', NULL, NULL, '0', '1',
 'D', '1', '1', NULL, 'layui-icon-template-1', '拼豆项目'),
(1890000000000000002, 'Evad', '2026-08-26 00:00:00', 'Evad', '2026-08-26 00:00:00', 0, 1, 0, NULL,
 '生成拼豆像素风图片', 1890000000000000001, 10, 'home', 'fusebean/home/index', NULL, '0', '1',
 'M', '1', '1', 'fusebean:home:index', 'layui-icon-template-1', '生成拼豆像素风图片');
