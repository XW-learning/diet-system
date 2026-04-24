package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.CommentSaveDTO;
import com.xw.dto.InteractDTO;
import com.xw.service.InteractService;
import com.xw.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "社区互动模块(赞、藏、评)")
@RestController
@RequestMapping("/api/interact")
public class InteractController {

    @Autowired
    private InteractService interactService;

    @Operation(summary = "37. 点赞/取消点赞")
    @LogOperation("点赞")
    @PostMapping("/like")
    public Result<String> toggleLike(@RequestBody InteractDTO dto) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return interactService.toggleLike(userId, dto);
    }

    @Operation(summary = "38. 收藏")
    @LogOperation("收藏")
    @PostMapping("/collect")
    public Result<String> toggleCollect(@RequestBody InteractDTO dto) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return interactService.toggleCollect(userId, dto);
    }

    @Operation(summary = "39. 发布评论/回复")
    @LogOperation("发布评论")
    @PostMapping("/comment")
    public Result<String> addComment(@RequestBody CommentSaveDTO dto) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return interactService.addComment(userId, dto);
    }

    @Operation(summary = "40. 增加动态分享/转发数")
    @LogOperation("增加动态分享")
    @PostMapping("/share/count")
    public Result<String> incrementShareCount(@RequestParam Long shareId) {
        return interactService.incrementShareCount(shareId);
    }
}