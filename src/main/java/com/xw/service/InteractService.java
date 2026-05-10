package com.xw.service;

import com.xw.dto.CommentSaveDTO;
import com.xw.dto.InteractDTO;

/**
 * 互动服务接口
 * @author XW
 */
public interface InteractService {

    String toggleLike(Long userId, InteractDTO dto);

    String toggleCollect(Long userId, InteractDTO dto);

    String addComment(Long userId, CommentSaveDTO dto);

    String incrementShareCount(Long shareId);
}
