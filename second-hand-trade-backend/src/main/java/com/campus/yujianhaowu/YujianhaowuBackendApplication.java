package com.campus.yujianhaowu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 豫见好物 - 河南文创产品销售平台后端启动类
 * 
 * @author yujianhaowu
 * @since 2026-03-09
 */
@SpringBootApplication
@MapperScan("com.campus.yujianhaowu.mapper")
@EnableAsync
@EnableScheduling
public class YujianhaowuBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujianhaowuBackendApplication.class, args);
        System.out.println("========================================");
        System.out.println("豫见好物平台启动成功！");
        System.out.println("API 文档地址：http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}
