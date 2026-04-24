package com.xw.controller; // 替换为你的实际包名

import com.xw.annotation.LogOperation;
import com.xw.common.Result; // 替换为你实际的 Result 路径
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload") // 对应前端调用的 /api/upload (因为前端有 /api 代理)
public class FileController {

    // 获取当前项目运行的根目录，并在其下创建一个 uploads 文件夹用来存图片
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Operation(summary = "上传文件")
    @LogOperation("上传文件")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        System.out.println("接收到文件上传请求");
        System.out.println("file: " + file);
        if (file.isEmpty()) {
            return Result.error("上传失败，请选择文件");
        }

        try {
            // 1. 确保存储文件夹存在，不存在则创建
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 获取原始文件名并提取后缀 (例如: .jpg, .png)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 3. 使用 UUID 生成新的文件名，防止文件名重复覆盖
            String newFileName = UUID.randomUUID().toString() + extension;

            // 4. 将文件保存到指定目录
            File dest = new File(UPLOAD_DIR + newFileName);
            file.transferTo(dest);

            // 5. 拼接图片的访问 URL
            // 假设你的后端运行在 8080 端口，前端代理会把 /api 转发过来
            // 所以我们返回一个相对根路径的地址 /uploads/xxx.jpg，这样前端可以直接渲染
            String imageUrl = "http://localhost:8080/uploads/" + newFileName;

            return Result.success(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传异常: " + e.getMessage());
        }
    }
}