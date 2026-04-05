package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.ShareSaveDTO;
import com.xw.vo.ShareVO;

import java.util.List;

/**
 * 分享(饮食动态) 业务接口
 * @author XW
 */
public interface ShareService {

    /**
     * 29. 发布或修改饮食动态分享
     * @param dto 包含文字内容、隐私设置和多张图片URL的 DTO
     * @return 成功或失败的提示
     */
    Result<String> saveShare(ShareSaveDTO dto);

    /**
     * 30. 删除指定的饮食动态
     * @param id 动态的记录ID
     * @param userId 当前操作用户的ID (用于越权校验，只能删自己的)
     * @return 成功或失败的提示
     */
    Result<String> deleteShare(Long id, Long userId);

    /**
     * 31. 获取大厅的分享列表 (最新公开动态)
     * 规则：只查 privacy=0 (公开) 且 auditStatus=1 (审核通过) 的数据
     * @return 组装好用户头像、昵称、配图列表的 VO 集合
     */
    Result<List<ShareVO>> getShareList();

    /**
     * 33. 获取我的分享列表
     * 规则：只查当前登录用户发布的，无论公私和审核状态
     * @param userId 当前用户ID
     * @return 组装好配图列表的 VO 集合
     */
    Result<List<ShareVO>> getMyShares(Long userId);
}