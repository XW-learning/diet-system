package com.xw.service;

import com.xw.common.Result;
import com.xw.entity.Plan;
import com.xw.vo.PlanDetailVO;

import java.util.List;

/**
 * 饮食方案业务接口
 * @author XW
 */
public interface PlanService {

    /**
     * 获取推荐饮食方案
     * 逻辑：根据用户的分类ID（categoryId）智能推荐方案
     * @param userId 用户ID
     * @return 推荐的方案实体
     */
    Result<List<Plan>> getRecommendPlan(Long userId);

    /**
     * 19. 更换方案 (随机推荐 5 个)
     */
    Result<List<Plan>> refreshPlan(Long userId);

    /**
     * 20. 获取方案详情 (包含菜品)
     */
    Result<PlanDetailVO> getPlanDetail(Long planId);

    /**
     * 21. 收藏/取消收藏方案
     */
    Result<String> favoritePlan(com.xw.dto.PlanFavoriteDTO dto);

    /**
     * 22. 获取收藏方案列表
     */
    Result<List<Plan>> getFavoritePlans(Long userId);
}