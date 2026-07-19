-- DeepSeek 对话模块 DDL

DROP TABLE IF EXISTS `chat_message`;
DROP TABLE IF EXISTS `chat_conversation`;

CREATE TABLE `chat_conversation` (
  `ID` varchar(64) NOT NULL COMMENT '会话ID',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `TITLE` varchar(200) DEFAULT '新对话' COMMENT '会话标题',
  `CHAT_MODE` varchar(32) DEFAULT NULL COMMENT '对话模式(runtime/fde)',
  `CREATED_AT` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATED_AT` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ID`),
  KEY `IDX_USER_ID` (`USER_ID`),
  KEY `IDX_UPDATED_AT` (`UPDATED_AT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';

CREATE TABLE `chat_message` (
  `ID` varchar(64) NOT NULL COMMENT '消息ID',
  `CONVERSATION_ID` varchar(64) NOT NULL COMMENT '会话ID',
  `ROLE` varchar(20) NOT NULL COMMENT '角色(user/assistant/status)',
  `CONTENT` text COMMENT '消息内容',
  `CARDS` text COMMENT '图表卡片JSON',
  `TIMESTAMP` varchar(30) DEFAULT NULL COMMENT '消息时间戳',
  PRIMARY KEY (`ID`),
  KEY `IDX_CONVERSATION_ID` (`CONVERSATION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话消息表';
