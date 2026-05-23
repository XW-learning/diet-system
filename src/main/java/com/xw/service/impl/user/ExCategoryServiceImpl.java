package com.xw.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.entity.user.ExCategory;
import com.xw.mapper.user.ExCategoryMapper;
import com.xw.service.user.ExCategoryService;
import org.springframework.stereotype.Service;

@Service
public class ExCategoryServiceImpl extends ServiceImpl<ExCategoryMapper, ExCategory> implements ExCategoryService {
}