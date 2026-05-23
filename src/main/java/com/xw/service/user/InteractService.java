package com.xw.service.user;

import com.xw.dto.user.CommentSaveDTO;
import com.xw.dto.user.InteractDTO;

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
