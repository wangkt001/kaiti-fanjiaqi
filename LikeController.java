package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(/likes)
@Tag(name = "点赞管理", description = "点赞相关接口")
public class LikeController {
    @PostMapping
    public Result<Void> like() { return Result.success(null); }
    @DeleteMapping
    public Result<Void> unlike() { return Result.success(null); }
}