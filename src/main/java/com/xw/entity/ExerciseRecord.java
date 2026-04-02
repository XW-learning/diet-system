package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_exercise_record")
public class ExerciseRecord {
    @TableId
    private Long id;
    private Long checkInId;
    private String exerciseName;
    private Integer duration;
    private Integer burnCalorie;
    private LocalDateTime createTime;
}