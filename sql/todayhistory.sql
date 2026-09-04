DROP TABLE IF EXISTS `today_history_link`;
DROP TABLE IF EXISTS `today_history_item`;
DROP TABLE IF EXISTS `today_history_page`;

CREATE TABLE `today_history_page` (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CREATOR` varchar(64) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `MODIFIER` varchar(64) DEFAULT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `LOGIC_DEL` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `REC_VER` bigint(20) DEFAULT '1' COMMENT '版本号',
  `BUILD_IN` tinyint(1) DEFAULT '0' COMMENT '系统内置（0-否1-是）',
  `TENANT_ID` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `MONTH_VALUE` int(11) NOT NULL COMMENT '月份',
  `DAY_VALUE` int(11) NOT NULL COMMENT '日期',
  `HISTORY_KEY` varchar(10) NOT NULL COMMENT '历史日期键（MM-DD）',
  `PAGE_TITLE` varchar(32) NOT NULL COMMENT '页面标题',
  `PAGE_URL` varchar(500) NOT NULL COMMENT '页面地址',
  `CONTENT_HASH` varchar(64) DEFAULT NULL COMMENT '页面内容摘要',
  `LAST_SYNC_TIME` datetime DEFAULT NULL COMMENT '最后同步时间',
  `ITEM_COUNT` int(11) DEFAULT '0' COMMENT '条目数量',
  `RAW_HTML` longtext COMMENT '抓取原始HTML',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uk_today_history_page_key` (`HISTORY_KEY`),
  KEY `idx_today_history_page_month_day` (`MONTH_VALUE`, `DAY_VALUE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历史上的今天-页面表';

CREATE TABLE `today_history_item` (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CREATOR` varchar(64) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `MODIFIER` varchar(64) DEFAULT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `LOGIC_DEL` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `REC_VER` bigint(20) DEFAULT '1' COMMENT '版本号',
  `BUILD_IN` tinyint(1) DEFAULT '0' COMMENT '系统内置（0-否1-是）',
  `TENANT_ID` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `PAGE_ID` bigint(20) NOT NULL COMMENT '页面ID',
  `HISTORY_KEY` varchar(10) NOT NULL COMMENT '历史日期键（MM-DD）',
  `ITEM_TYPE` varchar(20) NOT NULL COMMENT '条目类型',
  `ERA_TYPE` varchar(20) DEFAULT NULL COMMENT '时代类型',
  `SECTION_TITLE` varchar(64) DEFAULT NULL COMMENT '分组标题',
  `EVENT_YEAR` int(11) DEFAULT NULL COMMENT '年份',
  `SORT_NO` int(11) DEFAULT '1' COMMENT '排序号',
  `ITEM_HASH` varchar(64) NOT NULL COMMENT '条目哈希',
  `CONTENT` longtext COMMENT '条目内容',
  PRIMARY KEY (`ID`),
  KEY `idx_today_history_item_page_id` (`PAGE_ID`),
  KEY `idx_today_history_item_history_key` (`HISTORY_KEY`),
  KEY `idx_today_history_item_type` (`ITEM_TYPE`),
  UNIQUE KEY `uk_today_history_item_hash` (`PAGE_ID`, `ITEM_HASH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历史上的今天-条目表';

CREATE TABLE `today_history_link` (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CREATOR` varchar(64) DEFAULT NULL COMMENT '创建人',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `MODIFIER` varchar(64) DEFAULT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime DEFAULT NULL COMMENT '修改时间',
  `LOGIC_DEL` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
  `REC_VER` bigint(20) DEFAULT '1' COMMENT '版本号',
  `BUILD_IN` tinyint(1) DEFAULT '0' COMMENT '系统内置（0-否1-是）',
  `TENANT_ID` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `PAGE_ID` bigint(20) NOT NULL COMMENT '页面ID',
  `ITEM_ID` bigint(20) NOT NULL COMMENT '条目ID',
  `HISTORY_KEY` varchar(10) NOT NULL COMMENT '历史日期键（MM-DD）',
  `LINK_TEXT` varchar(255) DEFAULT NULL COMMENT '链接文本',
  `LINK_URL` varchar(1000) DEFAULT NULL COMMENT '链接地址',
  `LINK_TITLE` varchar(255) DEFAULT NULL COMMENT '链接标题',
  `INTERNAL_FLAG` tinyint(1) DEFAULT NULL COMMENT '是否站内链接',
  `SORT_NO` int(11) DEFAULT '1' COMMENT '排序号',
  `LINK_HASH` varchar(64) NOT NULL COMMENT '链接哈希',
  PRIMARY KEY (`ID`),
  KEY `idx_today_history_link_page_id` (`PAGE_ID`),
  KEY `idx_today_history_link_item_id` (`ITEM_ID`),
  KEY `idx_today_history_link_history_key` (`HISTORY_KEY`),
  UNIQUE KEY `uk_today_history_link_hash` (`ITEM_ID`, `LINK_HASH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='历史上的今天-链接表';
