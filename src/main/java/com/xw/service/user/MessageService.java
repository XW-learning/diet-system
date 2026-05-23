package com.xw.service.user;

import com.xw.vo.user.MessageVO;
import java.util.List;

public interface MessageService {

    Long getUnreadCount(Long userId);

    List<MessageVO> getMessageList(Long userId);

    String readMessage(Long id, Long userId);
}
