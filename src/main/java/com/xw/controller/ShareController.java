package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.ShareSaveDTO;
import com.xw.service.ShareService;
import com.xw.vo.ShareVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XW
 */
@Tag(name = "日常分享动态模块")
@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Operation(summary = "29. 发布/修改分享")
    @PostMapping("/save")
    public Result<String> saveShare(@RequestBody ShareSaveDTO dto) {
        return shareService.saveShare(dto);
    }

    @Operation(summary = "30. 删除分享")
    @DeleteMapping("/delete")
    public Result<String> deleteShare(@RequestParam Long id, @RequestParam Long userId) {
        return shareService.deleteShare(id, userId);
    }

    @Operation(summary = "31. 大厅分享列表(最新公开动态)")
    @GetMapping("/list")
    public Result<List<ShareVO>> getShareList() {
        return shareService.getShareList();
    }

    @Operation(summary = "33. 我的分享列表")
    @GetMapping("/my")
    public Result<List<ShareVO>> getMyShares(@RequestParam Long userId) {
        return shareService.getMyShares(userId);
    }
}