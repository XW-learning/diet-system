package com.xw.service.user;

import com.xw.dto.user.ShareSaveDTO;
import com.xw.vo.user.ShareVO;

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
