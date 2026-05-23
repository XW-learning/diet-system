package com.xw.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.dto.user.CustomPlanSaveDTO;
import com.xw.dto.user.DishReplaceDTO;
import com.xw.entity.user.Dish;
import com.xw.vo.user.DishVO;

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
