package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.ExerciseRecord;
import com.xw.dto.ExerciseCalendarDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface ExerciseRecordMapper extends BaseMapper<ExerciseRecord> {

    /**
     * 联合 t_check_in 表，查出某人某月的全部运动明细
     * 通过打卡记录关联查询指定用户、指定年月的运动数据
     */
    @Select("""
        SELECT
            c.date AS recordDate,
            e.exercise_name AS exerciseName,
            e.duration AS duration,
            e.burn_calorie AS burnCalorie
        FROM t_exercise_record e
        JOIN t_check_in c ON e.check_in_id = c.id
        WHERE c.user_id = #{userId}
          AND YEAR(c.date) = #{year}
          AND MONTH(c.date) = #{month}
    """)
    List<ExerciseCalendarDTO> selectMonthlyExerciseRecords(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month);
}
