package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.User;
import com.xw.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author XW
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 自定义多表联查：一次性获取用户的全量聚合信息
     * 巧妙使用 LEFT JOIN 和 LIMIT 1 子查询，直接映射为 UserVO
     */
    @Select("""
        SELECT
            u.id, u.phone, u.username, u.avatar, u.gender, u.age, u.height, u.email,
            c.name AS categoryName,
            b.weight, b.bmi, b.waist, b.hip, b.chest,
            t.target_weight AS targetWeight, t.target_date AS targetDate, t.goal_type AS goalType,
            p.taste, p.diet_type AS dietType
        FROM t_user u
        LEFT JOIN t_user_category c ON u.category_id = c.id
        LEFT JOIN (SELECT * FROM t_user_body_record WHERE user_id = #{userId} ORDER BY record_time DESC LIMIT 1) b ON u.id = b.user_id
        LEFT JOIN (SELECT * FROM t_user_target WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT 1) t ON u.id = t.user_id
        LEFT JOIN (SELECT * FROM t_user_preference WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT 1) p ON u.id = p.user_id
        WHERE u.id = #{userId}
    """)
    UserVO getUserFullInfo(@Param("userId") Long userId);
}