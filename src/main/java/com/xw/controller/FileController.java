package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    // 基础存储路径
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Operation(summary = "上传文件")
    @LogOperation("上传文件")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("上传失败，请选择文件");
        }

        try {
            // 1. 动态生成日期目录 (例如: "2024/05/20/")
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";

            // 2. 检查并创建带有日期的子目录
            File dir = new File(UPLOAD_DIR + datePath);
            if (!dir.exists()) {
                dir.mkdirs(); // mkdirs() 会级联创建所有不存在的父目录
            }

            // 3. 处理文件后缀并生成新的 UUID 文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 将文件保存到具体的日期子目录中
            File dest = new File(UPLOAD_DIR + datePath + newFileName);
            file.transferTo(dest);

            // 5. 动态拼接返回的 URL（解决 localhost 写死导致手机端无法访问的问题）
            String scheme = request.getScheme();             // http
            String serverName = request.getServerName();     // 获取真实的IP或域名
            int serverPort = request.getServerPort();        // 8080

            // 最终URL示例: http://192.168.1.100:8080/uploads/2024/05/20/xxxx.jpg
            String imageUrl = scheme + "://" + serverName + ":" + serverPort + "/uploads/" + datePath + newFileName;

            return Result.success(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传异常: " + e.getMessage());
        }
    }
}