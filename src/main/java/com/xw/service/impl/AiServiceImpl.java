package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xw.dto.AiChatDTO;
import com.xw.dto.AiFeedbackDTO;
import com.xw.entity.AiRecognize;
import com.xw.exception.BusinessException;
import com.xw.mapper.AiRecognizeMapper;
import com.xw.service.AiService;
import com.xw.service.PreferenceService;
import com.xw.service.UserService;
import com.xw.vo.AiDishVO;
import com.xw.vo.AllergyVO;
import com.xw.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author XW
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiRecognizeMapper aiRecognizeMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PreferenceService preferenceService;

    @Value("${zhipu.api-key}")
    private String apiKey;

    private static final String ZHIPU_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    @Override
    public AiDishVO recognizeImage(MultipartFile file, Long userId) {
        try {
            if (file == null || file.isEmpty()) {
                throw new BusinessException("请上传清晰的菜品图片");
            }

            byte[] fileBytes = file.getBytes();
            String base64String = java.util.Base64.getEncoder().encodeToString(fileBytes);
            String contentType = file.getContentType();
            if (contentType == null) contentType = "image/jpeg";
            String dataUrl = "data:" + contentType + ";base64," + base64String;

            String rootPath = System.getProperty("user.dir") + "/uploads";
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String dirPath = rootPath + "/" + userId + "/" + dateStr;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String ext = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            File destFile = new File(dir, fileName);
            Files.write(destFile.toPath(), fileBytes);

            String dbImageUrl = "/uploads/" + userId + "/" + dateStr + "/" + fileName;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "glm-4v-flash");

            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");

            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            Map<String, String> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", dataUrl);
            imageContent.put("image_url", imageUrlMap);

            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text",
                    "你是一位资深的健康营养师。\n" +
                            "请识别图片中的食物，并估算热量。\n\n" +
                            "⚠️ 重要要求：\n" +
                            "1. 只允许输出 JSON\n" +
                            "2. 不允许出现“例如”“说明”“结果如下”等任何文字\n" +
                            "3. 不允许换行\n" +
                            "4. 不允许 markdown\n\n" +
                            "格式如下：\n" +
                            "{\"dishName\":\"食物名称\",\"calorie\":整数}"
            );

            contents.add(imageContent);
            contents.add(textContent);
            userMessage.put("content", contents);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String aiResponseStr = restTemplate.postForObject(ZHIPU_API_URL, entity, String.class);

            JsonNode rootNode = objectMapper.readTree(aiResponseStr);
            String aiContent = rootNode.path("choices").get(0).path("message").path("content").asText();

            if (aiContent != null) {
                aiContent = aiContent.replaceAll("```json", "").replaceAll("```", "").trim();
                int start = aiContent.indexOf("{");
                int end = aiContent.lastIndexOf("}");
                if (start != -1 && end != -1) {
                    aiContent = aiContent.substring(start, end + 1);
                }
            }

            AiDishVO aiDishVO = objectMapper.readValue(aiContent, AiDishVO.class);

            AiRecognize recognize = new AiRecognize();
            recognize.setUserId(userId);
            recognize.setImageUrl(dbImageUrl);
            recognize.setResult(aiContent);
            recognize.setCalorie(aiDishVO.getCalorie());
            recognize.setCreateTime(LocalDateTime.now());
            aiRecognizeMapper.insert(recognize);

            aiDishVO.setRecordId(recognize.getId());
            return aiDishVO;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 识别失败 [用户ID:{}]: {}", userId, e.getMessage(), e);
            throw new BusinessException("AI 识别失败，请重试");
        }
    }

    @Override
    public String submitFeedback(AiFeedbackDTO dto) {
        UpdateWrapper<AiRecognize> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getRecordId())
                .eq("user_id", dto.getUserId())
                .set("calorie", dto.getCorrectCalorie());

        aiRecognizeMapper.update(null, updateWrapper);

        return "感谢反馈，已为您更新精准卡路里！";
    }

    @Override
    public SseEmitter streamChat(Long userId, AiChatDTO dto) {
        SseEmitter emitter = new SseEmitter(120000L);

        log.info("[AI聊天] userId={} message={}", userId, dto.getMessage());

        UserVO userVO = userService.getUserInfo(userId);
        List<AllergyVO> allergies = preferenceService.getUserAllergies(userId);
        String systemPrompt = buildSystemPrompt(userVO, allergies);

        log.info("[AI聊天] 用户数据 userVO={}", userVO);
        log.info("[AI聊天] 过敏食材 allergies={}", allergies != null ? allergies.size() + "条" : "无");
        log.info("[AI聊天] System Prompt 长度={} 前200字={}", systemPrompt.length(),
                systemPrompt.length() > 200 ? systemPrompt.substring(0, 200) + "..." : systemPrompt);

        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "glm-4-flash");
                requestBody.put("stream", true);

                List<Map<String, String>> messages = new ArrayList<>();
                messages.add(Map.of("role", "system", "content", systemPrompt));
                messages.add(Map.of("role", "user", "content", dto.getMessage()));
                requestBody.put("messages", messages);

                String jsonBody = objectMapper.writeValueAsString(requestBody);
                log.info("[AI聊天] 发送Zhipu请求体 长度={}", jsonBody.length());

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ZHIPU_API_URL))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
                log.info("[AI聊天] Zhipu HTTP状态码={}", response.statusCode());

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);

                            if ("[DONE]".equals(data)) {
                                log.info("[AI聊天] Zhipu 流式传输完成");
                                emitter.complete();
                                break;
                            }

                            JsonNode rootNode = objectMapper.readTree(data);
                            JsonNode choices = rootNode.path("choices");
                            if (choices.isArray() && !choices.isEmpty()) {
                                String content = choices.get(0).path("delta").path("content").asText();
                                if (content != null && !content.isEmpty()) {
                                    log.debug("[AI聊天] 发送chunk: {}", content);
                                    emitter.send(content);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("[AI聊天] 异常: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private String buildSystemPrompt(UserVO user, List<AllergyVO> allergies) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位极其专业的私人健康营养师，名字叫'XW健康管家'。\n\n");

        sb.append("## 用户身体数据\n");
        if (user != null) {
            String genderStr = "未知";
            if (user.getGender() != null) {
                genderStr = user.getGender() == 1 ? "男" : "女";
            }
            sb.append("- 性别：").append(genderStr).append("\n");
            if (user.getAge() != null) {
                sb.append("- 年龄：").append(user.getAge()).append(" 岁\n");
            }
            if (user.getHeight() != null) {
                sb.append("- 身高：").append(user.getHeight()).append(" cm\n");
            }
            if (user.getWeight() != null) {
                sb.append("- 体重：").append(user.getWeight()).append(" kg\n");
            }
            if (user.getBmi() != null) {
                sb.append("- BMI：").append(user.getBmi()).append("\n");
            }

            sb.append("\n## 用户饮食偏好\n");
            if (isNotEmpty(user.getTaste())) {
                sb.append("- 口味偏好：").append(user.getTaste()).append("\n");
            }
            if (isNotEmpty(user.getDietType())) {
                sb.append("- 饮食类型：").append(user.getDietType()).append("\n");
            }
            if (!isNotEmpty(user.getTaste()) && !isNotEmpty(user.getDietType())) {
                sb.append("- 暂无特殊偏好\n");
            }

            sb.append("\n## 用户健康目标\n");
            if (isNotEmpty(user.getGoalType())) {
                sb.append("- 目标类型：").append(user.getGoalType()).append("\n");
            }
            if (user.getTargetWeight() != null) {
                sb.append("- 目标体重：").append(user.getTargetWeight()).append(" kg\n");
            }
            if (!isNotEmpty(user.getGoalType()) && user.getTargetWeight() == null) {
                sb.append("- 暂无设定目标\n");
            }
        } else {
            sb.append("- 暂无身体数据\n");
        }

        sb.append("\n## 用户过敏/忌口食物\n");
        if (allergies != null && !allergies.isEmpty()) {
            StringBuilder allergyNames = new StringBuilder();
            for (int i = 0; i < allergies.size(); i++) {
                if (i > 0) allergyNames.append("、");
                allergyNames.append(allergies.get(i).getName());
            }
            sb.append("- ").append(allergyNames).append("\n");
        } else {
            sb.append("- 无\n");
        }

        sb.append("\n## 回答规则\n");
        sb.append("1. 严格根据用户的身体数据、饮食偏好、健康目标和忌口食物，提供个性化建议\n");
        sb.append("2. 绝对不要推荐用户过敏或忌口的食物，也不要推荐与此类食材相关的菜谱\n");
        sb.append("3. 回答要科学、温和、精简，排版清晰，适当使用分段和列表\n");
        sb.append("4. 只回答与健康饮食、运动健身、营养搭配相关的问题，遇到无关问题请礼貌拒绝\n");
        sb.append("5. 如果用户的身体数据不完整，可以温和地提醒用户完善个人信息以获得更精准的建议\n");

        return sb.toString();
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
