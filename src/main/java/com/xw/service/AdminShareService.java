package com.xw.service;

import com.xw.common.PageResult;
import com.xw.dto.AdminShareQueryDTO;
import com.xw.vo.AdminCommentVO;
import com.xw.vo.AdminShareVO;

import java.util.List;

public interface AdminShareService {
    PageResult<AdminShareVO> getShareList(AdminShareQueryDTO queryDTO);
    AdminShareVO getShareDetail(Long shareId);
    void auditShare(Long shareId, Integer auditStatus, String reason);
    void deleteShare(Long shareId);
    List<AdminCommentVO> getShareComments(Long shareId);
    void deleteComment(Long commentId);
}
