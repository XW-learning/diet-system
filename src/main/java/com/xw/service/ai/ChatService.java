package com.xw.service.ai;

import com.xw.dto.ai.ChatMessageDTO;
import com.xw.vo.ai.ChatHistoryFullVO;
import com.xw.vo.ai.ChatSummaryVO;

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
