package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.PlanFavoriteDTO;
import com.xw.dto.PlanSearchDTO;
import com.xw.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xw.mapper.*;
import com.xw.service.PlanService;
import com.xw.vo.MealVO;
import com.xw.vo.PlanDetailVO;
import com.xw.vo.PlanVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XW
 */
@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserPlanFavoriteMapper favoriteMapper;
    @Autowired
    private UserBodyRecordMapper bodyRecordMapper;
    @Autowired
    private UserPreferenceMapper preferenceMapper;
    @Autowired
    private UserCustomPlanMapper customPlanMapper;

    @Override
    public Result<List<Plan>> getRecommendPlan(Long userId) {
        return generateIntelligentPlans(userId, false);
    }

    @Override
    public Result<List<Plan>> refreshPlan(Long userId) {
        return generateIntelligentPlans(userId, true);
    }

    /**
     * 🌟 核心推荐引擎：融合了 BMI + 饮食偏好 的三级降级算法
     */
    private Result<List<Plan>> generateIntelligentPlans(Long userId, boolean isRandom) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getCategoryId() == null) {
            return Result.error("您尚未设置人群分类，无法为您推荐方案");
        }

        LambdaQueryWrapper<UserBodyRecord> bodyWrapper = new LambdaQueryWrapper<>();
        bodyWrapper.eq(UserBodyRecord::getUserId, userId).orderByDesc(UserBodyRecord::getRecordTime).last("LIMIT 1");
        UserBodyRecord latestBody = bodyRecordMapper.selectOne(bodyWrapper);

        LambdaQueryWrapper<UserPreference> prefWrapper = new LambdaQueryWrapper<>();
        prefWrapper.eq(UserPreference::getUserId, userId).orderByDesc(UserPreference::getCreateTime).last("LIMIT 1");
        UserPreference preference = preferenceMapper.selectOne(prefWrapper);

        LambdaQueryWrapper<Plan> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(Plan::getCategoryId, user.getCategoryId()).eq(Plan::getStatus, 1);

        if (latestBody != null && latestBody.getBmi() != null) {
            BigDecimal bmi = latestBody.getBmi();
            if (bmi.compareTo(new BigDecimal("24.0")) >= 0) {
                baseWrapper.le(Plan::getCalorieMax, 1600);
            } else if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                baseWrapper.ge(Plan::getCalorieMin, 2000);
            } else {
                baseWrapper.between(Plan::getCalorieMin, 1500, 2500);
            }
        }

        // 🟢 Level 1: 尝试匹配偏好
        if (preference != null) {
            LambdaQueryWrapper<Plan> level1Wrapper = baseWrapper.clone();
            boolean hasPref = false;

            if (preference.getTaste() != null && !preference.getTaste().isEmpty()) {
                level1Wrapper.like(Plan::getPrinciple, preference.getTaste());
                hasPref = true;
            }
            if (preference.getDietType() != null && !preference.getDietType().isEmpty()) {
                level1Wrapper.or().like(Plan::getPrinciple, preference.getDietType());
                hasPref = true;
            }

            if (hasPref) {
                applySorting(level1Wrapper, isRandom);
                List<Plan> level1Plans = planMapper.selectList(level1Wrapper);
                if (!level1Plans.isEmpty()) return Result.success(level1Plans);
            }
        }

        // 🟡 Level 2: 降级到仅使用 BMI 推荐
        LambdaQueryWrapper<Plan> level2Wrapper = baseWrapper.clone();
        applySorting(level2Wrapper, isRandom);
        List<Plan> level2Plans = planMapper.selectList(level2Wrapper);
        if (!level2Plans.isEmpty()) {
            return Result.success(level2Plans);
        }

        // 🔴 Level 3: 极限兜底
        LambdaQueryWrapper<Plan> level3Wrapper = new LambdaQueryWrapper<>();
        level3Wrapper.eq(Plan::getCategoryId, user.getCategoryId()).eq(Plan::getStatus, 1);
        applySorting(level3Wrapper, isRandom);
        List<Plan> level3Plans = planMapper.selectList(level3Wrapper);

        if (level3Plans.isEmpty()) return Result.error("系统暂时没有适合您的方案");

        return Result.success(level3Plans);
    }

    private void applySorting(LambdaQueryWrapper<Plan> wrapper, boolean isRandom) {
        if (isRandom) {
            wrapper.last("ORDER BY RAND() LIMIT 5");
        } else {
            wrapper.orderByDesc(Plan::getCreateTime).last("LIMIT 5");
        }
    }

    @Override
    public Result<PlanDetailVO> getPlanDetail(Long planId) {
        if (planId == null) return Result.error("方案ID不能为空");

        Plan plan = planMapper.selectById(planId);
        if (plan == null) return Result.error("方案不存在或已被下架");

        List<MealVO> meals = planMapper.getPlanMeals(planId);

        PlanDetailVO detailVO = new PlanDetailVO();
        detailVO.setPlan(plan);
        detailVO.setMeals(meals);

        return Result.success(detailVO);
    }

    @Override
    public Result<String> favoritePlan(Long userId, PlanFavoriteDTO dto) { // 🌟 接收绝对安全的 userId
        // 1. 移除了 dto.getUserId() == null 的校验
        if (dto.getPlanId() == null || dto.getAction() == null) {
            return Result.error("参数不完整");
        }

        // 2. 🌟 查询时强行绑定当前登录人 userId
        LambdaQueryWrapper<UserPlanFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlanFavorite::getUserId, userId)
                .eq(UserPlanFavorite::getTargetId, dto.getPlanId())
                .eq(UserPlanFavorite::getType, 2);
        UserPlanFavorite existingFav = favoriteMapper.selectOne(wrapper);

        if (dto.getAction() == 1) {
            // ---- 执行收藏逻辑 ----
            if (existingFav != null) return Result.error("您已经收藏过该方案了");

            UserPlanFavorite newFav = new UserPlanFavorite();
            newFav.setUserId(userId); // 🌟 插入时强行写入真实的 userId，防越权
            newFav.setTargetId(dto.getPlanId());
            newFav.setType(2);
            newFav.setCreateTime(LocalDateTime.now());
            favoriteMapper.insert(newFav);
            return Result.success("收藏成功");

        } else if (dto.getAction() == 0) {
            // ---- 执行取消收藏逻辑 ----
            if (existingFav == null) return Result.error("您尚未收藏该方案");

            favoriteMapper.deleteById(existingFav.getId());
            return Result.success("已取消收藏");
        }

        return Result.error("无效的操作指令");
    }

    @Override
    public Result<List<Plan>> getFavoritePlans(Long userId) {
        List<Plan> favoritePlans = planMapper.getUserFavoritePlans(userId);
        return Result.success(favoritePlans);
    }

    @Override
    public Result<List<PlanVO>> searchPlans(PlanSearchDTO dto) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Plan::getStatus, 1);

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            String safeKeyword = dto.getKeyword().trim().replace("'", "''");
            wrapper.and(w -> w
                    .like(Plan::getName, safeKeyword)
                    .or()
                    .like(Plan::getPrinciple, safeKeyword)
                    .or()
                    .inSql(Plan::getCategoryId,
                            "SELECT id FROM t_user_category WHERE name LIKE '%" + safeKeyword + "%'")
            );
        }

        if (dto.getGoal() != null && !dto.getGoal().trim().isEmpty()) {
            String safeGoal = dto.getGoal().trim().replace("'", "''");
            wrapper.inSql(Plan::getCategoryId,
                    "SELECT id FROM t_user_category WHERE name LIKE '%" + safeGoal + "%'");
        }

        if (dto.getMaxCalories() != null) {
            wrapper.le(Plan::getCalorieMax, dto.getMaxCalories());
        }

        wrapper.orderByDesc(Plan::getCreateTime);
        List<Plan> planList = planMapper.selectList(wrapper);

        List<PlanVO> voList = new ArrayList<>();
        for (Plan plan : planList) {
            PlanVO vo = new PlanVO();
            BeanUtils.copyProperties(plan, vo);
            voList.add(vo);
        }

        return Result.success(voList);
    }

    @Override
    public Result<List<com.xw.entity.UserCustomPlan>> getCustomPlans(Long userId) {
        LambdaQueryWrapper<com.xw.entity.UserCustomPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.xw.entity.UserCustomPlan::getUserId, userId)
                .orderByDesc(com.xw.entity.UserCustomPlan::getCreateTime);

        List<com.xw.entity.UserCustomPlan> customPlans = customPlanMapper.selectList(wrapper);
        return Result.success(customPlans);
    }
}