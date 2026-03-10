package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(/cultural-tags)
@Tag(name = "文化标签管理", description = "文化标签相关接口")
public class CulturalTagController {
    @GetMapping
    public Result<Object> getList() { return Result.success(null); }
}