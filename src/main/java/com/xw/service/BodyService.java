package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.BodyRecordDTO;

/**
 * @author XW
 */
public interface BodyService {
    /**
     * 保存身材记录（含 BMI 自动计算）
     */
    Result<String> saveRecord(BodyRecordDTO dto);

    /**
     * 获取指定用户的身材记录列表（按时间倒序）
     */
    Result<java.util.List<com.xw.entity.UserBodyRecord>> getRecordList(Long userId);

    /**
     * 删除指定的单条身材记录
     */
    Result<String> deleteRecord(Long id);
}