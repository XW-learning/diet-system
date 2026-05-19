package com.xw.service;

import com.xw.common.PageResult;
import com.xw.dto.AdminDishQueryDTO;
import com.xw.dto.AdminDishSaveDTO;
import com.xw.vo.AdminDishVO;

public interface AdminDishService {
    PageResult<AdminDishVO> getDishList(AdminDishQueryDTO queryDTO);
    void saveDish(AdminDishSaveDTO dto);
    void deleteDish(Long dishId);
}
