package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.CustomPlanSaveDTO;
import com.xw.dto.DishReplaceDTO;
import com.xw.entity.Dish;
import com.xw.vo.DishVO;

import java.util.List;

/**
 * @author XW
 */
public interface DishService {

    /**
     * 获取可选菜品列表
     */
    Result<List<Dish>> getDishList(String keyword);

    /**
     * 🌟 新增：搜索菜品列表（直接返回携带营养素的 VO 列表）
     * * @param keyword 搜索关键词
     * @return 菜品VO列表
     */
    List<DishVO> searchDish(String keyword);

    /**
     * 替换菜品（做过敏原校验并返回营养素）
     */
    Result<DishVO> replaceDish(DishReplaceDTO dto);

    /**
     * 保存用户自定义饮食方案
     */
    Result<String> saveCustomPlan(CustomPlanSaveDTO dto);

    /**
     * 搜索菜品列表（支持关键词和分类过滤）
     * @param keyword 搜索关键词
     * @param categoryId 分类ID
     * @return 菜品VO列表
     */
    List<DishVO> searchDish(String keyword, Integer categoryId);
}