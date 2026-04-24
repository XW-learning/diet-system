package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.service.MessageService;
import com.xw.utils.ThreadLocalUtil;
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
    public Result<Long> getUnreadCount() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return messageService.getUnreadCount(currentUserId);
    }

    @Operation(summary = "35. 获取消息列表")
    @GetMapping("/list")
    public Result<List<MessageVO>> getMessageList() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return messageService.getMessageList(currentUserId);
    }

    @Operation(summary = "36. 标记消息为已读")
    @LogOperation("标记消息为已读")
    @PutMapping("/read")
    public Result<String> readMessage(@RequestParam Long id) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return messageService.readMessage(id, currentUserId);
    }
}