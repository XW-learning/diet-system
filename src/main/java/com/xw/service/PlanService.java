package com.xw.service;

import com.xw.common.Result;
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

    Result<List<Plan>> getRecommendPlan(Long userId);

    Result<List<Plan>> refreshPlan(Long userId);

    Result<PlanDetailVO> getPlanDetail(Long planId);

    // 🌟 核心安全修改：加入上下文 userId，不再信任 DTO 传参
    Result<String> favoritePlan(Long userId, PlanFavoriteDTO dto);

    Result<List<Plan>> getFavoritePlans(Long userId);

    Result<List<PlanVO>> searchPlans(PlanSearchDTO dto);

    Result<List<UserCustomPlan>> getCustomPlans(Long userId);
}