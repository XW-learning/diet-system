package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.BodyRecordDTO;
import com.xw.entity.UserBodyRecord;

import java.util.List;

/**
 * 身体数据服务接口
 * @author XW
 */
public interface BodyService {

    /**
     * 保存/更新身体记录
     */
    Result<String> saveRecord(Long userId, BodyRecordDTO dto);

    /**
     * 获取历史记录列表
     */
    Result<List<UserBodyRecord>> getRecordList(Long userId);

    /**
     * 删除指定的记录
     * 🌟 修改点：增加了 userId 参数，用于防越权删除
     */
    Result<String> deleteRecord(Long userId, Long id);
}