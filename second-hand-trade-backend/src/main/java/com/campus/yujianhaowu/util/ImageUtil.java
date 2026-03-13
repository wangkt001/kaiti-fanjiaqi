package com.campus.yujianhaowu.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 图片工具类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
public class ImageUtil {

    /**
     * 将图片文件转换为 Base64 编码
     *
     * @param imagePath 图片路径
     * @return Base64 编码字符串
     */
    public static String imageToBase64(String imagePath) {
        try (FileInputStream fis = new FileInputStream(imagePath)) {
            byte[] imageBytes = fis.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("图片转 Base64 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 将图片文件转换为 Base64 编码
     *
     * @param imageFile 图片文件
     * @return Base64 编码字符串
     */
    public static String imageToBase64(File imageFile) {
        if (!imageFile.exists()) {
            log.error("图片文件不存在：{}", imageFile.getAbsolutePath());
            return null;
        }

        try (FileInputStream fis = new FileInputStream(imageFile)) {
            byte[] imageBytes = fis.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            log.info("图片转 Base64 成功，文件大小：{} bytes, Base64 长度：{}", imageBytes.length, base64.length());
            return base64;
        } catch (IOException e) {
            log.error("图片转 Base64 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 将 Base64 编码的图片保存为文件
     *
     * @param base64     Base64 编码字符串
     * @param outputPath 输出文件路径
     */
    public static void base64ToImage(String base64, String outputPath) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            File outputFile = new File(outputPath);
            
            // 确保目录存在
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            java.io.FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(imageBytes);
            fos.close();
            
            log.info("Base64 转图片成功：{}", outputPath);
        } catch (IOException e) {
            log.error("Base64 转图片失败：{}", e.getMessage());
        }
    }
}
