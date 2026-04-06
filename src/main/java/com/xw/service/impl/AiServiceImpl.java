package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xw.common.Result;
import com.xw.dto.AiChatDTO;
import com.xw.dto.AiFeedbackDTO;
import com.xw.entity.AiRecognize;
import com.xw.mapper.AiRecognizeMapper;
import com.xw.service.AiService;
import com.xw.vo.AiDishVO;
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

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiRecognizeMapper aiRecognizeMapper;

    // SpringBoot 自带的 JSON 解析神器
    @Autowired
    private ObjectMapper objectMapper;

    // 从 application.yml 读取 API Key
    @Value("${zhipu.api-key}")
    private String apiKey;

    // 智谱标准 V4 接口地址
    private static final String ZHIPU_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    @Override
    public Result<AiDishVO> recognizeImage(MultipartFile file, Long userId) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请上传清晰的菜品图片");
            }

            // 1. 获取文件的字节流 (用于给 AI 识别，也用于存本地)
            byte[] fileBytes = file.getBytes();
            String base64String = java.util.Base64.getEncoder().encodeToString(fileBytes);
            String contentType = file.getContentType();
            if (contentType == null) contentType = "image/jpeg";
            String dataUrl = "data:" + contentType + ";base64," + base64String;

            // =========================================================
            // 🌟 新增核心功能：将图片保存到本地磁盘
            // =========================================================
            // 1. 定义根目录 (System.getProperty("user.dir") 代表当前 SpringBoot 项目的根目录)
            String rootPath = System.getProperty("user.dir") + "/uploads";
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 2. 拼接你的天才目录结构：根目录 / userId / 日期 /
            String dirPath = rootPath + "/" + userId + "/" + dateStr;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs(); // 如果文件夹不存在，连环创建
            }

            // 3. 命名规则：UUID (保证全球唯一，防重名覆盖) + 原文件后缀
            String originalFilename = file.getOriginalFilename();
            String ext = ".jpg"; // 默认后缀兜底
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 4. 将刚才内存里的字节流，狠狠地写进硬盘里！
            File destFile = new File(dir, fileName);
            Files.write(destFile.toPath(), fileBytes);

            // 5. 生成存入数据库的相对 URL 路径 (前端将来通过这个路径访问图片)
            String dbImageUrl = "/uploads/" + userId + "/" + dateStr + "/" + fileName;

            // =========================================================
            // 往下是之前调用智谱大模型的逻辑 (保持不变)
            // =========================================================
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

            // =========================================================
            // 入库时，存入我们刚刚生成的真实 URL
            // =========================================================
            AiRecognize recognize = new AiRecognize();
            recognize.setUserId(userId);
            recognize.setImageUrl(dbImageUrl); // 🌟 存入：/uploads/1001/2026-04-06/uuid.jpg
            recognize.setResult(aiContent);
            recognize.setCalorie(aiDishVO.getCalorie());
            recognize.setCreateTime(LocalDateTime.now());
            aiRecognizeMapper.insert(recognize);

            aiDishVO.setRecordId(recognize.getId());
            return Result.success(aiDishVO);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("AI 识别失败，请检查图片或网络状态: " + e.getMessage());
        }
    }

    @Override
    public Result<String> submitFeedback(AiFeedbackDTO dto) {
        // 如果用户发现 AI 算错了，手动在前端修改，就会触发这个接口来纠正数据库
        UpdateWrapper<AiRecognize> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getRecordId())
                .eq("user_id", dto.getUserId()) // 安全防越权
                .set("calorie", dto.getCorrectCalorie()); // 覆盖为正确的卡路里

        aiRecognizeMapper.update(null, updateWrapper);

        // 架构师思考：其实这里可以异步将纠错数据扔进消息队列或日志，以后专门用来微调咱们自己的大模型！
        return Result.success("感谢反馈，已为您更新精准卡路里！");
    }

    @Override
    public SseEmitter streamChat(AiChatDTO dto) {
        // 1. 创建 SSE 发射器，设置超时时间为 2 分钟 (120000ms)
        SseEmitter emitter = new SseEmitter(120000L);

        // 2. 开启一个异步线程去请求大模型，防止阻塞 SpringBoot 的主线程
        CompletableFuture.runAsync(() -> {
            try {
                // 构建 JSON 请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "glm-4-flash"); // 聊天用 flash 模型，速度最快
                requestBody.put("stream", true); // 🌟 开启流式输出核心参数！

                List<Map<String, String>> messages = new ArrayList<>();
                // 赋予 AI 灵魂 (System Prompt)
                messages.add(Map.of("role", "system", "content",
                        "你是一位极其专业的私人健康营养师，名字叫'XW健康管家'。请根据用户的提问给出科学、温和的饮食和运动建议。回答要精简、排版清晰。"));
                // 用户的提问
                messages.add(Map.of("role", "user", "content", dto.getMessage()));
                requestBody.put("messages", messages);

                String jsonBody = objectMapper.writeValueAsString(requestBody);

                // 🌟 采用 Java 11 原生的 HttpClient 发起流式请求 (无任何第三方依赖)
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ZHIPU_API_URL))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                // 获取响应流
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                // 3. 逐行读取大模型返回的数据流
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 智谱返回的流式数据格式是以 "data: " 开头的
                        if (line.startsWith("data: ")) {
                            String data = line.substring(6);

                            // 如果流结束了，智谱会发送 [DONE]
                            if ("[DONE]".equals(data)) {
                                emitter.complete();
                                break;
                            }

                            // 解析这一个字 (delta) 的 JSON
                            JsonNode rootNode = objectMapper.readTree(data);
                            JsonNode choices = rootNode.path("choices");
                            if (choices.isArray() && !choices.isEmpty()) {
                                String content = choices.get(0).path("delta").path("content").asText();
                                if (content != null && !content.isEmpty()) {
                                    // 🌟 核心：拿到一个字，立刻通过 SSE 推送给前端！
                                    emitter.send(content);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // 发生异常时关闭流并报错
                emitter.completeWithError(e);
            }
        });

        // 立刻把 emitter 实例返回给前端，随后上面的异步线程会不断往里面塞数据
        return emitter;
    }
}