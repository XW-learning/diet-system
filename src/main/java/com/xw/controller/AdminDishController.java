package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.PageResult;
import com.xw.common.Result;
import com.xw.dto.AdminDishQueryDTO;
import com.xw.dto.AdminDishSaveDTO;
import com.xw.service.AdminDishService;
import com.xw.vo.AdminDishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Tag(name = "管理员-食品管理")
@RestController
@RequestMapping("/api/admin")
public class AdminDishController {

    @Autowired
    private AdminDishService adminDishService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/dish/";

    @Operation(summary = "57. 获取菜品列表（分页+搜索）")
    @GetMapping("/dish/list")
    public Result<PageResult<AdminDishVO>> getDishList(AdminDishQueryDTO queryDTO) {
        return Result.success(adminDishService.getDishList(queryDTO));
    }

    @Operation(summary = "58. 新增/修改菜品")
    @LogOperation("食品管理-新增/修改")
    @PostMapping("/dish/save")
    public Result<String> saveDish(@RequestBody AdminDishSaveDTO dto) {
        adminDishService.saveDish(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "59. 删除菜品")
    @LogOperation("食品管理-删除")
    @DeleteMapping("/dish/delete")
    public Result<String> deleteDish(@RequestParam Long dishId) {
        adminDishService.deleteDish(dishId);
        return Result.success("删除成功");
    }

    @Operation(summary = "60. 上传菜品图片（管理员专用）")
    @LogOperation("食品管理-上传图片")
    @PostMapping("/upload/dish")
    public Result<String> uploadDishImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("上传失败，请选择文件");
        }
        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/";
            File dir = new File(UPLOAD_DIR + datePath);
            if (!dir.exists()) dir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + extension;

            File dest = new File(UPLOAD_DIR + datePath + newFileName);
            file.transferTo(dest);

            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String imageUrl = scheme + "://" + serverName + ":" + serverPort + "/uploads/dish/" + datePath + newFileName;

            return Result.success(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传异常: " + e.getMessage());
        }
    }
}
