package com.xw.controller.admin;

import com.xw.annotation.LogOperation;
import com.xw.common.PageResult;
import com.xw.common.Result;
import com.xw.dto.admin.AdminPlanMealSaveDTO;
import com.xw.dto.admin.AdminPlanQueryDTO;
import com.xw.dto.admin.AdminPlanSaveDTO;
import com.xw.service.admin.AdminPlanService;
import com.xw.vo.admin.AdminDishOptionVO;
import com.xw.vo.admin.AdminPlanDetailVO;
import com.xw.vo.admin.AdminPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员-食谱管理")
@RestController
@RequestMapping("/api/admin")
public class AdminPlanController {

    @Autowired
    private AdminPlanService adminPlanService;

    @Operation(summary = "50. 获取食谱列表（分页+搜索）")
    @GetMapping("/plan/list")
    public Result<PageResult<AdminPlanVO>> getPlanList(AdminPlanQueryDTO queryDTO) {
        return Result.success(adminPlanService.getPlanList(queryDTO));
    }

    @Operation(summary = "51. 获取食谱详情（含餐次和菜品）")
    @GetMapping("/plan/detail")
    public Result<AdminPlanDetailVO> getPlanDetail(@RequestParam Long planId) {
        return Result.success(adminPlanService.getPlanDetail(planId));
    }

    @Operation(summary = "52. 新增/修改食谱")
    @LogOperation("食谱管理-新增/修改")
    @PostMapping("/plan/save")
    public Result<String> savePlan(@RequestBody AdminPlanSaveDTO dto) {
        adminPlanService.savePlan(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "53. 启用/禁用食谱")
    @LogOperation("食谱管理-启用/禁用")
    @PutMapping("/plan/status")
    public Result<String> updatePlanStatus(@RequestParam Long planId, @RequestParam Integer status) {
        adminPlanService.updatePlanStatus(planId, status);
        return Result.success("操作成功");
    }

    @Operation(summary = "54. 删除食谱")
    @LogOperation("食谱管理-删除")
    @DeleteMapping("/plan/delete")
    public Result<String> deletePlan(@RequestParam Long planId) {
        adminPlanService.deletePlan(planId);
        return Result.success("删除成功");
    }

    @Operation(summary = "55. 保存食谱餐次和菜品分配")
    @LogOperation("食谱管理-餐次分配")
    @PostMapping("/plan/meal/save")
    public Result<String> savePlanMeals(@RequestBody AdminPlanMealSaveDTO dto) {
        adminPlanService.savePlanMeals(dto);
        return Result.success("保存成功");
    }

    @Operation(summary = "56. 获取可选菜品列表（用于餐次分配）")
    @GetMapping("/dish/options")
    public Result<List<AdminDishOptionVO>> getDishOptions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(adminPlanService.getDishOptions(keyword, categoryId));
    }
}
