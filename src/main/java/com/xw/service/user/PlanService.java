package com.xw.service.user;

import com.xw.dto.user.PlanFavoriteDTO;
import com.xw.dto.user.PlanSearchDTO;
import com.xw.entity.user.Plan;
import com.xw.entity.user.UserCustomPlan;
import com.xw.vo.user.PlanDetailVO;
import com.xw.vo.user.PlanVO;

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

    String deactivatePlan(Long userId);
}
