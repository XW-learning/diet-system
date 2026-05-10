package com.xw.service;

import com.xw.vo.MessageVO;
import java.util.List;

public interface MessageService {

    Long getUnreadCount(Long userId);

    List<MessageVO> getMessageList(Long userId);

    String readMessage(Long id, Long userId);
}
