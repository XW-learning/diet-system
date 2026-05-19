package com.xw.service;

import com.xw.common.PageResult;
import com.xw.dto.AdminPlanMealSaveDTO;
import com.xw.dto.AdminPlanQueryDTO;
import com.xw.dto.AdminPlanSaveDTO;
import com.xw.vo.AdminDishOptionVO;
import com.xw.vo.AdminPlanDetailVO;
import com.xw.vo.AdminPlanVO;

import java.util.List;

public interface AdminPlanService {
    PageResult<AdminPlanVO> getPlanList(AdminPlanQueryDTO queryDTO);
    AdminPlanDetailVO getPlanDetail(Long planId);
    void savePlan(AdminPlanSaveDTO dto);
    void updatePlanStatus(Long planId, Integer status);
    void deletePlan(Long planId);
    void savePlanMeals(AdminPlanMealSaveDTO dto);
    List<AdminDishOptionVO> getDishOptions(String keyword, Long categoryId);
}
