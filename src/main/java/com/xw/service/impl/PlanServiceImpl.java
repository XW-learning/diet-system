package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.PlanFavoriteDTO;
import com.xw.dto.PlanSearchDTO;
import com.xw.entity.*;
import com.xw.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.xw.mapper.*;
import com.xw.service.PlanService;
import com.xw.vo.MealVO;
import com.xw.vo.PlanDetailVO;
import com.xw.vo.PlanVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private UserPlanRecordMapper userPlanRecordMapper;

    @Override
    public List<Plan> getRecommendPlan(Long userId) {
        return generateIntelligentPlans(userId, false);
    }

    @Override
    public List<Plan> refreshPlan(Long userId) {
        return generateIntelligentPlans(userId, true);
    }

    private List<Plan> generateIntelligentPlans(Long userId, boolean isRandom) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getCategoryId() == null) {
            throw new BusinessException("Cannot fetch recommended plan because user category is missing.");
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
                if (!level1Plans.isEmpty()) return level1Plans;
            }
        }

        LambdaQueryWrapper<Plan> level2Wrapper = baseWrapper.clone();
        applySorting(level2Wrapper, isRandom);
        List<Plan> level2Plans = planMapper.selectList(level2Wrapper);
        if (!level2Plans.isEmpty()) {
            return level2Plans;
        }

        LambdaQueryWrapper<Plan> level3Wrapper = new LambdaQueryWrapper<>();
        level3Wrapper.eq(Plan::getCategoryId, user.getCategoryId()).eq(Plan::getStatus, 1);
        applySorting(level3Wrapper, isRandom);
        List<Plan> level3Plans = planMapper.selectList(level3Wrapper);

        if (level3Plans.isEmpty()) throw new BusinessException("No plan available.");
        return level3Plans;
    }

    private void applySorting(LambdaQueryWrapper<Plan> wrapper, boolean isRandom) {
        if (isRandom) {
            wrapper.last("ORDER BY RAND() LIMIT 5");
        } else {
            wrapper.orderByDesc(Plan::getCreateTime).last("LIMIT 5");
        }
    }

    @Override
    public PlanDetailVO getPlanDetail(Long planId) {
        if (planId == null) throw new BusinessException("Missing ID");
        Plan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("Plan not found");
        List<MealVO> meals = planMapper.getPlanMeals(planId);
        PlanDetailVO detailVO = new PlanDetailVO();
        detailVO.setPlan(plan);
        detailVO.setMeals(meals);
        return detailVO;
    }

    @Override
    public String favoritePlan(Long userId, PlanFavoriteDTO dto) {
        if (dto.getPlanId() == null || dto.getAction() == null) {
            throw new BusinessException("Missing parameters");
        }
        LambdaQueryWrapper<UserPlanFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlanFavorite::getUserId, userId)
                .eq(UserPlanFavorite::getTargetId, dto.getPlanId())
                .eq(UserPlanFavorite::getType, 2);
        UserPlanFavorite existingFav = favoriteMapper.selectOne(wrapper);

        if (dto.getAction() == 1) {
            if (existingFav != null) throw new BusinessException("Already collected");
            UserPlanFavorite newFav = new UserPlanFavorite();
            newFav.setUserId(userId);
            newFav.setTargetId(dto.getPlanId());
            newFav.setType(2);
            newFav.setCreateTime(LocalDateTime.now());
            favoriteMapper.insert(newFav);
            return "Collected successfully";
        } else if (dto.getAction() == 0) {
            if (existingFav == null) throw new BusinessException("Not collected yet");
            favoriteMapper.deleteById(existingFav.getId());
            return "Canceled successfully";
        }
        throw new BusinessException("Invalid action");
    }

    @Override
    public List<Plan> getFavoritePlans(Long userId) {
        List<Plan> favoritePlans = planMapper.getUserFavoritePlans(userId);
        return favoritePlans;
    }

    @Override
    public List<PlanVO> searchPlans(PlanSearchDTO dto) {
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
            if (plan.getTags() != null && !plan.getTags().isEmpty()) {
                vo.setTagList(Arrays.asList(plan.getTags().split(",")));
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<com.xw.entity.UserCustomPlan> getCustomPlans(Long userId) {
        LambdaQueryWrapper<com.xw.entity.UserCustomPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.xw.entity.UserCustomPlan::getUserId, userId)
                .orderByDesc(com.xw.entity.UserCustomPlan::getCreateTime);
        List<com.xw.entity.UserCustomPlan> customPlans = customPlanMapper.selectList(wrapper);
        return customPlans;
    }

    @Override
    public String activatePlan(Long userId, Long planId) {
        Plan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("Plan not found");

        LambdaQueryWrapper<UserPlanRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlanRecord::getUserId, userId)
                .eq(UserPlanRecord::getStatus, 1);
        UserPlanRecord activeRecord = userPlanRecordMapper.selectOne(queryWrapper);
        if (activeRecord != null) {
            activeRecord.setStatus(0);
            userPlanRecordMapper.updateById(activeRecord);
        }

        UserPlanRecord newRecord = new UserPlanRecord();
        newRecord.setUserId(userId);
        newRecord.setPlanId(planId);
        newRecord.setStatus(1);
        newRecord.setCreateTime(LocalDateTime.now());
        userPlanRecordMapper.insert(newRecord);

        plan.setUsageCount((plan.getUsageCount() == null ? 0 : plan.getUsageCount()) + 1);
        planMapper.updateById(plan);

        return "Recipe plan activated successfully";
    }

    @Override
    public PlanDetailVO getActivePlan(Long userId) {
        LambdaQueryWrapper<UserPlanRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlanRecord::getUserId, userId)
                .eq(UserPlanRecord::getStatus, 1)
                .orderByDesc(UserPlanRecord::getCreateTime)
                .last("LIMIT 1");
        UserPlanRecord activeRecord = userPlanRecordMapper.selectOne(queryWrapper);
        if (activeRecord == null) throw new BusinessException("No active plan");

        return getPlanDetail(activeRecord.getPlanId());
    }

    @Override
    public String deactivatePlan(Long userId) {
        LambdaQueryWrapper<UserPlanRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlanRecord::getUserId, userId)
                .eq(UserPlanRecord::getStatus, 1);
        UserPlanRecord activeRecord = userPlanRecordMapper.selectOne(queryWrapper);
        if (activeRecord == null) throw new BusinessException("No active plan to deactivate");

        activeRecord.setStatus(0);
        userPlanRecordMapper.updateById(activeRecord);
        return "Recipe plan deactivated successfully";
    }
}
