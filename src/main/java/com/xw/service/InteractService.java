package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.CommentSaveDTO;
import com.xw.dto.InteractDTO;

/**
 * 互动服务接口
 * @author XW
 */
public interface InteractService {

    // 🌟 新增 userId 参数
    Result<String> toggleLike(Long userId, InteractDTO dto);

    // 🌟 新增 userId 参数
    Result<String> toggleCollect(Long userId, InteractDTO dto);

    // 🌟 新增 userId 参数
    Result<String> addComment(Long userId, CommentSaveDTO dto);

    Result<String> incrementShareCount(Long shareId);
}