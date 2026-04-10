package com.xw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.entity.ExCategory;
import com.xw.mapper.ExCategoryMapper;
import com.xw.service.ExCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ExCategoryServiceImpl extends ServiceImpl<ExCategoryMapper, ExCategory> implements ExCategoryService {
}