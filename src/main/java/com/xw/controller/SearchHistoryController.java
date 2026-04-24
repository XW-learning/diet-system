package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.entity.SearchHistory;
import com.xw.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "获取当前用户的搜索历史10条")
    @LogOperation("获取当前用户的搜索历史")
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

    @Operation(summary = "清空当前用户的所有历史")
    @LogOperation("清空当前用户的所有历史")
    @DeleteMapping("/clear")
    public Result clearHistory(@RequestHeader("token") String token) {
        Long userId = 1L;
        searchHistoryService.lambdaUpdate().eq(SearchHistory::getUserId, userId).remove();
        return Result.success(null);
    }
}
