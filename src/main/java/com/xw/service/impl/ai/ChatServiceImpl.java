package com.xw.service.impl.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xw.dto.ai.ChatMessageDTO;
import com.xw.entity.ai.ChatMessage;
import com.xw.entity.ai.ChatSummary;
import com.xw.mapper.ai.ChatMessageMapper;
import com.xw.mapper.ai.ChatSummaryMapper;
import com.xw.service.ai.ChatService;
import com.xw.vo.ai.ChatHistoryFullVO;
import com.xw.vo.ai.ChatHistoryVO;
import com.xw.vo.ai.ChatSummaryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author XW
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatSummaryMapper chatSummaryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${zhipu.api-key}")
    private String apiKey;

    private static final String ZHIPU_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final int COMPRESS_THRESHOLD = 150;
    private static final int COMPRESS_BATCH = 100;

    private static final Map<Long, Object> compressLocks = new HashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveMessages(Long userId, List<ChatMessageDTO> messages) {
        if (messages == null || messages.isEmpty()) {
            return "无消息需要保存";
        }

        List<ChatMessage> entities = new ArrayList<>();
        for (ChatMessageDTO dto : messages) {
            ChatMessage msg = new ChatMessage();
            msg.setUserId(userId);
            msg.setRole(dto.getRole());
            msg.setContent(dto.getContent());
            msg.setCreateTime(dto.getCreateTime() != null ? dto.getCreateTime() : LocalDateTime.now());
            entities.add(msg);
        }

        for (ChatMessage entity : entities) {
            chatMessageMapper.insert(entity);
        }

        log.info("[Chat] 批量保存消息 userId={} count={}", userId, entities.size());

        tryTriggerCompress(userId);

        return "保存成功";
    }

    @Override
    public ChatHistoryFullVO getHistory(Long userId, int limit) {
        ChatHistoryFullVO vo = new ChatHistoryFullVO();

        List<ChatMessage> messages = chatMessageMapper.getHistory(userId, limit);
        List<ChatHistoryVO> historyList = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage msg = messages.get(i);
            ChatHistoryVO hvo = new ChatHistoryVO();
            hvo.setRole(msg.getRole());
            hvo.setContent(msg.getContent());
            hvo.setCreateTime(msg.getCreateTime());
            historyList.add(hvo);
        }
        vo.setMessages(historyList);

        List<ChatSummary> summaries = chatSummaryMapper.getRecentSummaries(userId, 10);
        List<ChatSummaryVO> summaryList = summaries.stream().map(s -> {
            ChatSummaryVO svo = new ChatSummaryVO();
            svo.setId(s.getId());
            svo.setSummary(s.getSummary());
            svo.setMessageCount(s.getMessageCount());
            svo.setCreateTime(s.getCreateTime());
            return svo;
        }).collect(Collectors.toList());
        vo.setSummaries(summaryList);

        return vo;
    }

    @Override
    public List<ChatSummaryVO> getSummaries(Long userId) {
        List<ChatSummary> summaries = chatSummaryMapper.getRecentSummaries(userId, 10);
        return summaries.stream().map(s -> {
            ChatSummaryVO svo = new ChatSummaryVO();
            svo.setId(s.getId());
            svo.setSummary(s.getSummary());
            svo.setMessageCount(s.getMessageCount());
            svo.setCreateTime(s.getCreateTime());
            return svo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String clearHistory(Long userId) {
        LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(ChatMessage::getUserId, userId);
        chatMessageMapper.delete(msgWrapper);

        LambdaQueryWrapper<ChatSummary> summaryWrapper = new LambdaQueryWrapper<>();
        summaryWrapper.eq(ChatSummary::getUserId, userId);
        chatSummaryMapper.delete(summaryWrapper);

        log.info("[Chat] 清空聊天记录 userId={}", userId);
        return "聊天记录已清空";
    }

    private void tryTriggerCompress(Long userId) {
        synchronized (getCompressLock(userId)) {
            int totalCount = chatMessageMapper.countByUser(userId);
            if (totalCount < COMPRESS_THRESHOLD) {
                return;
            }

            Long lastCompressedId = chatSummaryMapper.getLastCompressedMsgId(userId);
            int unCompressedCount = chatMessageMapper.countAfterId(userId, lastCompressedId);
            if (unCompressedCount < COMPRESS_BATCH) {
                return;
            }

            log.info("[Chat] 触发压缩 userId={} unCompressedCount={}", userId, unCompressedCount);

            List<ChatMessage> toCompress = chatMessageMapper.getMessagesAfterId(userId, lastCompressedId, COMPRESS_BATCH);
            if (toCompress.isEmpty()) {
                return;
            }

            String summaryText = generateSummary(toCompress);
            if (summaryText == null) {
                log.warn("[Chat] 压缩摘要生成失败 userId={}", userId);
                return;
            }

            ChatSummary summary = new ChatSummary();
            summary.setUserId(userId);
            summary.setSummary(summaryText);
            summary.setMessageCount(toCompress.size());
            summary.setStartMsgId(toCompress.get(0).getId());
            summary.setEndMsgId(toCompress.get(toCompress.size() - 1).getId());
            summary.setCreateTime(LocalDateTime.now());
            chatSummaryMapper.insert(summary);

            log.info("[Chat] 压缩完成 userId={} summaryId={} count={}", userId, summary.getId(), toCompress.size());
        }
    }

    private String generateSummary(List<ChatMessage> messages) {
        try {
            StringBuilder chatText = new StringBuilder();
            for (ChatMessage msg : messages) {
                String role = "user".equals(msg.getRole()) ? "用户" : "AI助手";
                chatText.append(role).append("：").append(msg.getContent()).append("\n");
            }

            String prompt = "请用100～200字中文总结以下对话的核心内容、用户的身体状况关注点和饮食偏好，只返回总结文本不要任何额外说明：\n\n" + chatText;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "glm-4-flash");

            List<Map<String, String>> msgList = new ArrayList<>();
            msgList.add(Map.of("role", "user", "content", prompt));
            requestBody.put("messages", msgList);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String responseStr = restTemplate.postForObject(ZHIPU_API_URL, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(responseStr);
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                String content = choices.get(0).path("message").path("content").asText();
                if (content != null && !content.trim().isEmpty()) {
                    return content.trim();
                }
            }
            return null;
        } catch (Exception e) {
            log.error("[Chat] 生成摘要异常: {}", e.getMessage(), e);
            return null;
        }
    }

    private Object getCompressLock(Long userId) {
        synchronized (compressLocks) {
            return compressLocks.computeIfAbsent(userId, k -> new Object());
        }
    }
}
