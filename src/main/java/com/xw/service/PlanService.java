package com.xw.service;

import com.xw.dto.PlanFavoriteDTO;
import com.xw.dto.PlanSearchDTO;
import com.xw.entity.Plan;
import com.xw.entity.UserCustomPlan;
import com.xw.vo.PlanDetailVO;
import com.xw.vo.PlanVO;

import java.util.List;

/**
 * 方案推荐与查询服务接口
 * @author XW
 */
public interface PlanService {

    List<Plan> getRecommendPlan(Long userId);

    List<Plan> refreshPlan(Long userId);

    PlanDetailVO getPlanDetail(Long planId);

    String favoritePlan(Long userId, PlanFavoriteDTO dto);

    List<Plan> getFavoritePlans(Long userId);

    List<PlanVO> searchPlans(PlanSearchDTO dto);

    List<UserCustomPlan> getCustomPlans(Long userId);

    String activatePlan(Long userId, Long planId);

    PlanDetailVO getActivePlan(Long userId);
}
