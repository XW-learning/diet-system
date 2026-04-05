package com.xw.service;

import com.xw.common.Result;
import com.xw.vo.MessageVO;
import java.util.List;

public interface MessageService {

    // 获取当前用户的未读消息总数
    Result<Long> getUnreadCount(Long userId);

    // 获取当前用户的消息列表
    Result<List<MessageVO>> getMessageList(Long userId);

    // 将某条消息设为已读
    Result<String> readMessage(Long id, Long userId);
}