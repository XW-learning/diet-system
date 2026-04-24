package com.xw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.entity.SearchHistory;
import com.xw.mapper.SearchHistoryMapper;
import com.xw.service.SearchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 搜索历史 Service 实现类
 */
@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory> implements SearchHistoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateHistory(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String finalKeyword = keyword.trim();

        // 1. 查询该用户是否已经搜索过这个词
        SearchHistory existHistory = this.lambdaQuery()
                .eq(SearchHistory::getUserId, userId)
                .eq(SearchHistory::getKeyword, finalKeyword)
                .one();

        if (existHistory != null) {
            // 2. 如果存在，只需要更新时间，这样它就能排在历史记录的最前面
            existHistory.setCreateTime(LocalDateTime.now());
            this.updateById(existHistory);
        } else {
            // 3. 如果不存在，插入一条全新的记录
            SearchHistory newHistory = new SearchHistory();
            newHistory.setUserId(userId);
            newHistory.setKeyword(finalKeyword);
            newHistory.setCreateTime(LocalDateTime.now());
            this.save(newHistory);
        }
    }
}