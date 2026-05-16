package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.ChatMessageDTO;
import com.xw.service.ChatService;
import com.xw.utils.ThreadLocalUtil;
import com.xw.vo.ChatHistoryFullVO;
import com.xw.vo.ChatSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XW
 */
@Tag(name = "AI聊天记录管理")
@RestController
@RequestMapping("/api/ai/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "批量保存聊天消息")
    @PostMapping("/save")
    public Result<String> saveMessages(@RequestBody List<ChatMessageDTO> messages) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return Result.success(chatService.saveMessages(userId, messages));
    }

    @Operation(summary = "加载聊天历史（含摘要）")
    @GetMapping("/history")
    public Result<ChatHistoryFullVO> getHistory(@RequestParam(defaultValue = "50") int limit) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return Result.success(chatService.getHistory(userId, limit));
    }

    @Operation(summary = "获取最近10次压缩摘要")
    @GetMapping("/summaries")
    public Result<List<ChatSummaryVO>> getSummaries() {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return Result.success(chatService.getSummaries(userId));
    }

    @Operation(summary = "清空聊天记录")
    @DeleteMapping("/clear")
    public Result<String> clearHistory() {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return Result.success(chatService.clearHistory(userId));
    }
}
