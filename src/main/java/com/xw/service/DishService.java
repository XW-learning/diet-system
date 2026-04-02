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
     * 替换菜品（做过敏原校验并返回营养素）
     */
    Result<DishVO> replaceDish(DishReplaceDTO dto);

    /**
     * 保存用户自定义饮食方案
     */
    Result<String> saveCustomPlan(CustomPlanSaveDTO dto);
}