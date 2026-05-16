package com.xw.service;

import com.xw.dto.ChatMessageDTO;
import com.xw.vo.ChatHistoryFullVO;
import com.xw.vo.ChatSummaryVO;

import java.util.List;

/**
 * @author XW
 */
public interface ChatService {
    String saveMessages(Long userId, List<ChatMessageDTO> messages);
    ChatHistoryFullVO getHistory(Long userId, int limit);
    List<ChatSummaryVO> getSummaries(Long userId);
    String clearHistory(Long userId);
}
