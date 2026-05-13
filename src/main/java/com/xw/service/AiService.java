package com.xw.service;

import com.xw.dto.AiChatDTO;
import com.xw.dto.AiFeedbackDTO;
import com.xw.vo.AiDishVO;
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
