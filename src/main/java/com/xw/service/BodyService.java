package com.xw.service;

import com.xw.dto.BodyRecordDTO;
import com.xw.entity.UserBodyRecord;

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
