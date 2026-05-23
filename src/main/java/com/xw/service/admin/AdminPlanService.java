package com.xw.service.admin;

import com.xw.common.PageResult;
import com.xw.dto.admin.AdminPlanMealSaveDTO;
import com.xw.dto.admin.AdminPlanQueryDTO;
import com.xw.dto.admin.AdminPlanSaveDTO;
import com.xw.vo.admin.AdminDishOptionVO;
import com.xw.vo.admin.AdminPlanDetailVO;
import com.xw.vo.admin.AdminPlanVO;

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
