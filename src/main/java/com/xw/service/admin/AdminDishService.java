package com.xw.service.admin;

import com.xw.common.PageResult;
import com.xw.dto.admin.AdminDishQueryDTO;
import com.xw.dto.admin.AdminDishSaveDTO;
import com.xw.vo.admin.AdminDishVO;

public interface AdminDishService {
    PageResult<AdminDishVO> getDishList(AdminDishQueryDTO queryDTO);
    void saveDish(AdminDishSaveDTO dto);
    void deleteDish(Long dishId);
}
