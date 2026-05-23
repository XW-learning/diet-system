-- =============================================
-- 管理员端新功能：食谱餐次管理表
-- 执行日期：2026-05-16
-- =============================================

-- 餐次分组表（每个食谱有多个餐次）
CREATE TABLE IF NOT EXISTS t_plan_meal_group (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id     BIGINT      NOT NULL COMMENT '关联食谱ID(t_diet_plan.id)',
    meal_name   VARCHAR(64) NOT NULL COMMENT '餐次名称，如"活力早餐"',
    meal_type   INT         NOT NULL COMMENT '餐次类型：1=早餐 2=午餐 3=晚餐 4=加餐',
    sort_order  INT         DEFAULT 0 COMMENT '排序（数字越小越靠前）',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_plan_id (plan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱餐次分组表';

-- 餐次菜品关联表（每个餐次包含多道菜品）
CREATE TABLE IF NOT EXISTS t_plan_meal_dish (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    meal_group_id BIGINT         NOT NULL COMMENT '关联餐次分组ID(t_plan_meal_group.id)',
    dish_id       BIGINT         NOT NULL COMMENT '关联菜品ID(t_dish.id)',
    weight        DECIMAL(10,2)  DEFAULT NULL COMMENT '份量(克)',
    INDEX idx_meal_group (meal_group_id),
    INDEX idx_dish (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐次菜品关联表';
