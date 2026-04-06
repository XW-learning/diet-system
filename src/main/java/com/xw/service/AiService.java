package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.AiChatDTO;
import com.xw.dto.AiFeedbackDTO;
import com.xw.dto.AiRecognizeDTO;
import com.xw.vo.AiDishVO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 智能识别与交互服务接口
 * @author XW
 */
public interface AiService {

    // 🌟 将 DTO 换成 MultipartFile 和 userId
    Result<AiDishVO> recognizeImage(MultipartFile file, Long userId);

    /**
     * 39. 提交纠正反馈 (修改大模型识别错误的卡路里)
     * @param dto 包含记录ID和正确卡路里的入参
     * @return 成功或失败提示
     */
    Result<String> submitFeedback(AiFeedbackDTO dto);

    /**
     * 40. AI 健康顾问实时聊天 (流式 SSE)
     * @param dto 聊天参数
     * @return SseEmitter 流对象
     */
    SseEmitter streamChat(AiChatDTO dto);
}