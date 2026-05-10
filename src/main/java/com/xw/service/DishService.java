package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
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

    List<Dish> getDishList(String keyword);

    List<DishVO> searchDish(String keyword);

    List<DishVO> searchDish(String keyword, Integer categoryId);

    DishVO replaceDish(Long userId, DishReplaceDTO dto);

    String saveCustomPlan(Long userId, CustomPlanSaveDTO dto);
}
