package com.xw.service;

import com.xw.dto.CategoryDTO;
import com.xw.vo.CategoryVO;
import java.util.List;

public interface CategoryService {
    void addCategory(CategoryDTO categoryDTO);
    void updateCategory(CategoryDTO categoryDTO);
    void deleteCategory(Integer id);
    List<CategoryVO> getCategoryList();
}
