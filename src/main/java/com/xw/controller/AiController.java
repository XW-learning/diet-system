package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.AiChatDTO;
import com.xw.dto.AiFeedbackDTO;
import com.xw.dto.AiRecognizeDTO;
import com.xw.service.AiService;
import com.xw.vo.AiDishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author XW
 */
@Tag(name = "AI 智能识别与交互模块")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Operation(summary = "38. AI 拍照识别食物与卡路里")
    @PostMapping(value = "/recognize", consumes = "multipart/form-data")
    public Result<AiDishVO> recognizeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        return aiService.recognizeImage(file, userId);
    }

    @Operation(summary = "39. 提交纠正反馈")
    @PostMapping("/feedback")
    public Result<String> submitFeedback(@RequestBody AiFeedbackDTO dto) {
        return aiService.submitFeedback(dto);
    }

    @Operation(summary = "40. AI 健康顾问实时聊天 (流式打字机效果)")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody AiChatDTO dto) {
        return aiService.streamChat(dto);
    }
}