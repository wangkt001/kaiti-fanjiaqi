package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(/cultural-contents)
@Tag(name = "文化资讯管理", description = "文化资讯相关接口")
public class CulturalContentController {
    @GetMapping
    public Result<Object> getList() { return Result.success(null); }
}