package com.xw.service.ai;

import com.xw.dto.ai.AiChatDTO;
import com.xw.dto.ai.AiFeedbackDTO;
import com.xw.vo.ai.AiDishVO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 智能识别与交互服务接口
 * @author XW
 */
public interface AiService {

    AiDishVO recognizeImage(MultipartFile file, Long userId);

    String submitFeedback(AiFeedbackDTO dto);

    SseEmitter streamChat(Long userId, AiChatDTO dto);
}
