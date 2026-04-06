package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.entity.SearchHistory;

/**
 * 搜索历史 Service 接口
 */
public interface SearchHistoryService extends IService<SearchHistory> {

    /**
     * 保存或更新搜索历史
     * 如果用户已经搜过该词，则更新搜索时间为最新；如果是新词，则插入新记录。
     *
     * @param userId  用户ID
     * @param keyword 搜索关键词
     */
    void saveOrUpdateHistory(Long userId, String keyword);
}