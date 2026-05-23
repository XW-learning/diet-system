package com.xw.service.user;

import com.xw.dto.user.CategoryDTO;
import com.xw.vo.user.CategoryVO;
import java.util.List;

public interface CategoryService {
    void addCategory(CategoryDTO categoryDTO);
    void updateCategory(CategoryDTO categoryDTO);
    void deleteCategory(Integer id);
    List<CategoryVO> getCategoryList();
}
