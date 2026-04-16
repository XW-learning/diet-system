package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.CheckIn;
import com.xw.entity.CheckInStat;
import com.xw.vo.AiDishVO;
import com.xw.vo.CheckInDetailVO;
import com.xw.vo.CheckInSummaryVO;
import com.xw.vo.FitnessCalendarVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡与追踪模块业务接口
 * @author XW
 */
public interface CheckInService {

    /**
     * 1. 获取热量看板汇总数据
     * @param userId 用户ID
     * @param date 日期
     * @return 包含预算、摄入、消耗和剩余的看板数据
     */
    Result<CheckInSummaryVO> getSummary(Long userId, LocalDate date);

    /**
     * 2. 饮食打卡 (早/中/晚/加餐)
     * @param dto 饮食打卡请求参数
     * @return 成功提示
     */
    Result<String> doMealCheckIn(MealCheckInDTO dto);

    /**
     * 2.1 批量饮食打卡 (多食物合并提交)
     * @param dtoList 饮食打卡请求参数列表
     * @return 成功提示
     */
    Result<String> doMealCheckInBatch(List<MealCheckInDTO> dtoList);

    /**
     * 3. 运动打卡 (记录运动消耗热量)
     * @param dto 运动打卡请求参数
     * @return 成功提示
     */
    Result<String> doExerciseCheckIn(ExerciseCheckInDTO dto);



    /**
     * 5. 获取某日打卡明细详情
     */
    Result<CheckInDetailVO> getCheckInDetail(Long userId, LocalDate date);

    /**
     * 6. 获取打卡历史日历列表
     */
    Result<List<CheckIn>> getCheckInList(Long userId);

    /**
     * 7. 获取打卡统计与状态
     */
    Result<CheckInStat> getCheckInStat(Long userId);

    /**
     * 获取指定日期的详细饮食分析数据
     * @param userId 用户ID
     * @param dateStr 日期 (yyyy-MM-dd)
     */
    Result<com.xw.vo.CheckInAnalysisVO> getDailyAnalysis(Long userId, String dateStr);

    /**
     * 获取健身日历专属聚合数据
     */
    Result<FitnessCalendarVO> getFitnessCalendarData(Long userId, Integer year, Integer month);
}