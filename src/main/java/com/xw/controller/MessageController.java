package com.xw.controller;

import com.xw.common.Result;
import com.xw.service.MessageService;
import com.xw.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XW
 */
@Tag(name = "消息互动中心模块")
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Operation(summary = "34. 获取未读消息数量(用于红点提示)")
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(@RequestParam Long userId) {
        return messageService.getUnreadCount(userId);
    }

    @Operation(summary = "35. 获取消息列表")
    @GetMapping("/list")
    public Result<List<MessageVO>> getMessageList(@RequestParam Long userId) {
        return messageService.getMessageList(userId);
    }

    @Operation(summary = "36. 标记消息为已读")
    @PutMapping("/read")
    public Result<String> readMessage(@RequestParam Long id, @RequestParam Long userId) {
        return messageService.readMessage(id, userId);
    }
}