-- DeepSeek 对话模块 DDL（基于代码生成器BasePo结构）

DROP TABLE IF EXISTS `chat_message`;
DROP TABLE IF EXISTS `chat_conversation`;

CREATE TABLE `chat_conversation` (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CREATOR` varchar(64) NOT NULL COMMENT '创建人',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `MODIFIER` varchar(64) NOT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime NOT NULL COMMENT '修改时间',
  `LOGIC_DEL` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `REC_VER` bigint(20) NOT NULL COMMENT '版本号',
  `BUILD_IN` tinyint(1) NULL DEFAULT 0 COMMENT '系统内置',
  `TENANT_ID` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  `PROVIDER_CODE` varchar(32) DEFAULT NULL COMMENT '服务商编码',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `TITLE` varchar(200) DEFAULT '新对话' COMMENT '会话标题',
  `CHAT_MODE` varchar(32) DEFAULT NULL COMMENT '对话模式',
  `EXT_ID` varchar(64) DEFAULT NULL COMMENT '客户端会话标识',
  PRIMARY KEY (`ID`),
  KEY `IDX_EXT_ID` (`EXT_ID`),
  KEY `IDX_USER_ID` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交互式会话';

CREATE TABLE `chat_message` (
  `ID` bigint(20) NOT NULL COMMENT '主键',
  `CREATOR` varchar(64) NOT NULL COMMENT '创建人',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `MODIFIER` varchar(64) NOT NULL COMMENT '修改人',
  `MODIFY_TIME` datetime NOT NULL COMMENT '修改时间',
  `LOGIC_DEL` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `REC_VER` bigint(20) NOT NULL COMMENT '版本号',
  `BUILD_IN` tinyint(1) NULL DEFAULT 0 COMMENT '系统内置',
  `TENANT_ID` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  `CONVERSATION_ID` bigint(20) NOT NULL COMMENT '会话ID',
  `ROLE` varchar(20) NOT NULL COMMENT '角色(user/assistant)',
  `CONTENT` text COMMENT '消息内容',
  `ORDER_NUM` int(11) DEFAULT 0 COMMENT '排序号',
  PRIMARY KEY (`ID`),
  KEY `IDX_CONVERSATION_ID` (`CONVERSATION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交互式会话-消息';
