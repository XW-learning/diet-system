package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.common.Result;
import com.xw.dto.CustomPlanSaveDTO;
import com.xw.dto.DishReplaceDTO;
import com.xw.entity.Dish;
import com.xw.vo.DishVO;

import java.util.List;

/**
 * 菜品服务接口
 * @author XW
 */
public interface DishService extends IService<Dish> {

    Result<List<Dish>> getDishList(String keyword);

    List<DishVO> searchDish(String keyword);

    List<DishVO> searchDish(String keyword, Integer categoryId);

    // 🌟 修改：新增 userId 参数，抛弃对 DTO 内部 userId 的依赖
    Result<DishVO> replaceDish(Long userId, DishReplaceDTO dto);

    // 🌟 修改：新增 userId 参数，防越权保存方案
    Result<String> saveCustomPlan(Long userId, CustomPlanSaveDTO dto);
}