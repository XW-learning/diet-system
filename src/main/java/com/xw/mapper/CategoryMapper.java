package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // 继承 BaseMapper 后，无需手写任何简单的 CRUD SQL
    // 自动拥有 insert, deleteById, updateById, selectList 等方法
}