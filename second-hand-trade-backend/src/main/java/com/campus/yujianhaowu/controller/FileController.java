package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件上传相关接口")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.url.prefix}")
    private String fileUrlPrefix;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("仅支持图片上传");
        }

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            return Result.error("创建上传目录失败");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String filename = UUID.randomUUID() + (StringUtils.hasText(extension) ? "." + extension : "");

        File targetFile = new File(uploadDir, filename);
        file.transferTo(targetFile);

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrlPrefix + "/" + filename);
        result.put("filename", filename);
        return Result.success(result);
    }
}
