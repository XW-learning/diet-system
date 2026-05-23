package com.xw.service.user;

import com.xw.dto.user.BodyRecordDTO;
import com.xw.entity.user.UserBodyRecord;

import java.util.List;

/**
 * 身体数据服务接口
 * @author XW
 */
public interface BodyService {

    String saveRecord(Long userId, BodyRecordDTO dto);

    List<UserBodyRecord> getRecordList(Long userId);

    String deleteRecord(Long userId, Long id);
}
