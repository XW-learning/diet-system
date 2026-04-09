package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author XW
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
    /**
     * 🌟 原子累加摄入热量
     * SQL: UPDATE t_check_in SET total_calorie = total_calorie + #{cal} WHERE id = #{id}
     */
    @Update("UPDATE t_check_in SET total_calorie = total_calorie + #{cal} WHERE id = #{id}")
    int atomicIncrementCalorie(@Param("id") Long id, @Param("cal") Integer cal);
}