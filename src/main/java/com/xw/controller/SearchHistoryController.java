package com.xw.controller;

import com.xw.common.Result;
import com.xw.entity.SearchHistory;
import com.xw.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XW
 */
@RestController
@RequestMapping("/api/search/history")
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService searchHistoryService;

    // 获取当前用户的搜索历史（取最近10条）
    @GetMapping("/list")
    public Result getHistory(@RequestHeader("token") String token) {
        // 伪代码：根据token获取userId
        Long userId = 1L;
        List<SearchHistory> list = searchHistoryService.lambdaQuery()
                .eq(SearchHistory::getUserId, userId)
                .orderByDesc(SearchHistory::getCreateTime)
                .last("limit 10")
                .list();
        return Result.success(list);
    }

    // 清空当前用户的所有历史
    @DeleteMapping("/clear")
    public Result clearHistory(@RequestHeader("token") String token) {
        Long userId = 1L;
        searchHistoryService.lambdaUpdate().eq(SearchHistory::getUserId, userId).remove();
        return Result.success(null);
    }
}
