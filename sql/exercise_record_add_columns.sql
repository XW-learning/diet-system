-- 为 t_exercise_record 表添加 user_id 和 record_date 字段
-- 使其不再依赖 JOIN t_check_in 即可获取用户和日期信息

-- 1. 添加字段
ALTER TABLE t_exercise_record
  ADD COLUMN user_id BIGINT DEFAULT NULL COMMENT '用户ID' AFTER check_in_id,
  ADD COLUMN record_date DATE DEFAULT NULL COMMENT '运动日期' AFTER user_id;

-- 2. 从 t_check_in 回填已有数据
UPDATE t_exercise_record e
  JOIN t_check_in c ON e.check_in_id = c.id
SET e.user_id = c.user_id,
    e.record_date = c.date;

-- 3. 清理 check_in_id 无效的孤儿记录（可选，根据业务需要决定）
-- DELETE FROM t_exercise_record WHERE user_id IS NULL;
