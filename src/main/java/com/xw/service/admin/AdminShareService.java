package com.xw.service.admin;

import com.xw.common.PageResult;
import com.xw.dto.admin.AdminShareQueryDTO;
import com.xw.vo.admin.AdminCommentVO;
import com.xw.vo.admin.AdminShareVO;

import java.util.List;

public interface AdminShareService {
    PageResult<AdminShareVO> getShareList(AdminShareQueryDTO queryDTO);
    AdminShareVO getShareDetail(Long shareId);
    void auditShare(Long shareId, Integer auditStatus, String reason);
    void deleteShare(Long shareId);
    List<AdminCommentVO> getShareComments(Long shareId);
    void deleteComment(Long commentId);
}
