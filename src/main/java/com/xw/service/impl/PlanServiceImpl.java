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
        return generateIntelligentPlans(userId, false); // false代表首页推荐
    }

    @Override
    public Result<List<Plan>> refreshPlan(Long userId) {
        return generateIntelligentPlans(userId, true);  // true代表换一换(打乱顺序)
    }

    /**
     * 🌟 核心推荐引擎：融合了 BMI + 饮食偏好 的三级降级算法
     */
    private Result<List<Plan>> generateIntelligentPlans(Long userId, boolean isRandom) {
        if (userId == null) return Result.error("用户ID不能为空");

        User user = userMapper.selectById(userId);
        if (user == null || user.getCategoryId() == null) {
            return Result.error("您尚未设置人群分类，无法为您推荐方案");
        }

        // 1. 获取最新 BMI
        LambdaQueryWrapper<UserBodyRecord> bodyWrapper = new LambdaQueryWrapper<>();
        bodyWrapper.eq(UserBodyRecord::getUserId, userId).orderByDesc(UserBodyRecord::getRecordTime).last("LIMIT 1");
        UserBodyRecord latestBody = bodyRecordMapper.selectOne(bodyWrapper);

        // 2. 获取用户饮食偏好 (作为软性建议)
        LambdaQueryWrapper<UserPreference> prefWrapper = new LambdaQueryWrapper<>();
        prefWrapper.eq(UserPreference::getUserId, userId).orderByDesc(UserPreference::getCreateTime).last("LIMIT 1");
        UserPreference preference = preferenceMapper.selectOne(prefWrapper);

        // 3. 构建基础查询条件 (必须满足分类和启用状态)
        LambdaQueryWrapper<Plan> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(Plan::getCategoryId, user.getCategoryId()).eq(Plan::getStatus, 1);

        // 4. 叠加 BMI 卡路里限制
        if (latestBody != null && latestBody.getBmi() != null) {
            BigDecimal bmi = latestBody.getBmi();
            if (bmi.compareTo(new BigDecimal("24.0")) >= 0) {
                baseWrapper.le(Plan::getCalorieMax, 1600); // 胖子卡热量
            } else if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                baseWrapper.ge(Plan::getCalorieMin, 2000); // 瘦子多吃点
            } else {
                baseWrapper.between(Plan::getCalorieMin, 1500, 2500); // 正常人均衡
            }
        }

        // ================== 核心漏斗策略 ==================

        // 🟢 Level 1: 尝试匹配偏好 (口味 / 饮食类型)
        if (preference != null) {
            // 克隆一个查询器，避免污染基础条件
            LambdaQueryWrapper<Plan> level1Wrapper = baseWrapper.clone();
            boolean hasPref = false;

            // 如果有偏好，我们在方案的【说明 principle】中模糊匹配他的口味（如"清淡","高蛋白"）
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
                // 只要查到了符合口味的方案，直接返回！
                if (!level1Plans.isEmpty()) return Result.success(level1Plans);
            }
        }

        // 🟡 Level 2: 口味没匹配上（或没填口味），降级到仅使用 BMI 推荐
        LambdaQueryWrapper<Plan> level2Wrapper = baseWrapper.clone();
        applySorting(level2Wrapper, isRandom);
        List<Plan> level2Plans = planMapper.selectList(level2Wrapper);
        if (!level2Plans.isEmpty()) {
            return Result.success(level2Plans);
        }

        // 🔴 Level 3: 连 BMI 匹配出来的方案都没有，极限兜底：仅根据人群分类乱推
        LambdaQueryWrapper<Plan> level3Wrapper = new LambdaQueryWrapper<>();
        level3Wrapper.eq(Plan::getCategoryId, user.getCategoryId()).eq(Plan::getStatus, 1);
        applySorting(level3Wrapper, isRandom);
        List<Plan> level3Plans = planMapper.selectList(level3Wrapper);

        if (level3Plans.isEmpty()) return Result.error("系统暂时没有适合您的方案");

        return Result.success(level3Plans);
    }

    /**
     * 辅助方法：统一处理排序逻辑 (换一换随机排，首页按最新排)
     */
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

        // 1. 查基础方案信息
        Plan plan = planMapper.selectById(planId);
        if (plan == null) return Result.error("方案不存在或已被下架");

        // 2. 查该方案关联的菜品列表
        List<MealVO> meals = planMapper.getPlanMeals(planId);

        // 3. 完美组装 VO
        PlanDetailVO detailVO = new PlanDetailVO();
        detailVO.setPlan(plan);
        detailVO.setMeals(meals);

        return Result.success(detailVO);
    }

    @Override
    public Result<String> favoritePlan(PlanFavoriteDTO dto) {
        if (dto.getUserId() == null || dto.getPlanId() == null || dto.getAction() == null) {
            return Result.error("参数不完整");
        }

        // 1. 先查查是否已经有这条收藏记录了
        LambdaQueryWrapper<UserPlanFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlanFavorite::getUserId, dto.getUserId())
                .eq(UserPlanFavorite::getTargetId, dto.getPlanId())
                .eq(UserPlanFavorite::getType, 2);
        UserPlanFavorite existingFav = favoriteMapper.selectOne(wrapper);

        if (dto.getAction() == 1) {
            // ---- 执行收藏逻辑 ----
            if (existingFav != null) return Result.error("您已经收藏过该方案了");

            UserPlanFavorite newFav = new UserPlanFavorite();
            newFav.setUserId(dto.getUserId());
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
        if (userId == null) return Result.error("用户ID不能为空");

        // 直接调用我们在第一步手写的联表 SQL
        List<Plan> favoritePlans = planMapper.getUserFavoritePlans(userId);
        return Result.success(favoritePlans);
    }

    @Override
    public Result<List<PlanVO>> searchPlans(PlanSearchDTO dto) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>();

        // 1. 安全底线：只允许查询已启用(status=1)的方案
        wrapper.eq(Plan::getStatus, 1);

        // 2. 核心：一框多搜逻辑 (关键词匹配 名称 OR 原则 OR 分类名称)
        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            // 简单的防注入替换
            String safeKeyword = dto.getKeyword().trim().replace("'", "''");

            // 使用 and(w -> w.xxx.or().xxx) 生成带有括号的 SQL，防止与 status=1 冲突
            wrapper.and(w -> w
                    .like(Plan::getName, safeKeyword)
                    .or()
                    .like(Plan::getPrinciple, safeKeyword)
                    .or()
                    // 魔法指令：直接在子查询里通过关键词找 t_user_category 的分类ID
                    .inSql(Plan::getCategoryId,
                            "SELECT id FROM t_user_category WHERE name LIKE '%" + safeKeyword + "%'")
            );
        }

        // 3. 目标过滤 (如前端明确传了 goal="减肥")
        if (dto.getGoal() != null && !dto.getGoal().trim().isEmpty()) {
            String safeGoal = dto.getGoal().trim().replace("'", "''");
            wrapper.inSql(Plan::getCategoryId,
                    "SELECT id FROM t_user_category WHERE name LIKE '%" + safeGoal + "%'");
        }

        // 4. 最高卡路里限制
        if (dto.getMaxCalories() != null) {
            wrapper.le(Plan::getCalorieMax, dto.getMaxCalories());
        }

        // 5. 按最新发布时间排序
        wrapper.orderByDesc(Plan::getCreateTime);

        // 执行查询
        List<Plan> planList = planMapper.selectList(wrapper);

        // 6. 将 Entity 转化为 VO 返回给前端
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
        // 1. 参数校验
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 构造查询条件：查该用户的所有自定义方案，并按创建时间倒序（最新的在最上面）
        LambdaQueryWrapper<com.xw.entity.UserCustomPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.xw.entity.UserCustomPlan::getUserId, userId)
                .orderByDesc(com.xw.entity.UserCustomPlan::getCreateTime);

        // 3. 执行查询
        List<com.xw.entity.UserCustomPlan> customPlans = customPlanMapper.selectList(wrapper);

        return Result.success(customPlans);
    }
}