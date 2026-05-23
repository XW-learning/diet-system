package com.xw.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.user.ExerciseRecord;
import com.xw.dto.user.ExerciseCalendarDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface ExerciseRecordMapper extends BaseMapper<ExerciseRecord> {

    /**
     * 查询指定用户、指定年月的全部运动明细
     * 直接从 t_exercise_record 表查询，不再依赖 t_check_in 的 JOIN
     */
    @Select("""
        SELECT
            e.record_date AS recordDate,
            e.exercise_name AS exerciseName,
            e.duration AS duration,
            e.burn_calorie AS burnCalorie
        FROM t_exercise_record e
        WHERE e.user_id = #{userId}
          AND YEAR(e.record_date) = #{year}
          AND MONTH(e.record_date) = #{month}
    """)
    List<ExerciseCalendarDTO> selectMonthlyExerciseRecords(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month);

    /**
     * 查询指定用户、指定日期范围内的全部运动明细
     */
    @Select("""
        SELECT
            e.record_date AS recordDate,
            e.exercise_name AS exerciseName,
            e.duration AS duration,
            e.burn_calorie AS burnCalorie
        FROM t_exercise_record e
        WHERE e.user_id = #{userId}
          AND e.record_date >= #{startDate}
          AND e.record_date <= #{endDate}
        ORDER BY e.record_date, e.create_time
    """)
    List<ExerciseCalendarDTO> selectRecordsByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
