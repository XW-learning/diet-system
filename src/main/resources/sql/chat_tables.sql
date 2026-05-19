-- 聊天消息表（永久保留，不删除）
CREATE TABLE IF NOT EXISTS t_chat_message (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(10) NOT NULL COMMENT 'user / ai',
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 聊天压缩摘要表（只保留最近10次）
CREATE TABLE IF NOT EXISTS t_chat_summary (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    summary TEXT NOT NULL COMMENT 'AI生成的对话摘要',
    message_count INT DEFAULT 0 COMMENT '覆盖了多少条消息',
    start_msg_id BIGINT COMMENT '压缩批次起始消息ID',
    end_msg_id BIGINT COMMENT '压缩批次结束消息ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
