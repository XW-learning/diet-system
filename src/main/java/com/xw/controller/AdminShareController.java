package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.PageResult;
import com.xw.common.Result;
import com.xw.dto.AdminShareQueryDTO;
import com.xw.service.AdminShareService;
import com.xw.vo.AdminCommentVO;
import com.xw.vo.AdminShareVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员-社区管理")
@RestController
@RequestMapping("/api/admin")
public class AdminShareController {

    @Autowired
    private AdminShareService adminShareService;

    @Operation(summary = "61. 获取动态列表（分页+筛选）")
    @GetMapping("/share/list")
    public Result<PageResult<AdminShareVO>> getShareList(AdminShareQueryDTO queryDTO) {
        return Result.success(adminShareService.getShareList(queryDTO));
    }

    @Operation(summary = "62. 获取动态详情")
    @GetMapping("/share/detail")
    public Result<AdminShareVO> getShareDetail(@RequestParam Long shareId) {
        return Result.success(adminShareService.getShareDetail(shareId));
    }

    @Operation(summary = "63. 审核动态（通过/驳回）")
    @LogOperation("社区管理-审核动态")
    @PutMapping("/share/audit")
    public Result<String> auditShare(
            @RequestParam Long shareId,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String reason) {
        adminShareService.auditShare(shareId, auditStatus, reason);
        return Result.success(auditStatus == 1 ? "审核通过" : "已驳回");
    }

    @Operation(summary = "64. 删除动态")
    @LogOperation("社区管理-删除动态")
    @DeleteMapping("/share/delete")
    public Result<String> deleteShare(@RequestParam Long shareId) {
        adminShareService.deleteShare(shareId);
        return Result.success("删除成功");
    }

    @Operation(summary = "65. 获取动态的评论列表")
    @GetMapping("/share/comments")
    public Result<List<AdminCommentVO>> getShareComments(@RequestParam Long shareId) {
        return Result.success(adminShareService.getShareComments(shareId));
    }

    @Operation(summary = "66. 删除评论")
    @LogOperation("社区管理-删除评论")
    @DeleteMapping("/comment/delete")
    public Result<String> deleteComment(@RequestParam Long commentId) {
        adminShareService.deleteComment(commentId);
        return Result.success("删除成功");
    }
}
