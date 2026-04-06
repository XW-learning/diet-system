package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 搜索历史 Mapper 接口
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}