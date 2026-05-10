package com.xw.service;

import com.xw.dto.ShareSaveDTO;
import com.xw.vo.ShareVO;

import java.util.List;

/**
 * 分享(饮食动态) 业务接口
 * @author XW
 */
public interface ShareService {

    String saveShare(ShareSaveDTO dto);

    String deleteShare(Long id, Long userId);

    List<ShareVO> getShareList();

    List<ShareVO> getMyShares(Long userId);
}
