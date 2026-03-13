package com.campus.yujianhaowu.config;

import com.campus.yujianhaowu.service.CulturalContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动时初始化文化资讯数据
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializationRunner implements ApplicationRunner {

    private final CulturalContentService culturalContentService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化文化资讯数据...");
        culturalContentService.initializeData();
        log.info("文化资讯数据初始化完成");
    }
}
