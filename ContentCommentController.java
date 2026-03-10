package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content-comments")
@Tag(name = "资讯评论管理", description = "资讯评论相关接口")
public class ContentCommentController {
    @GetMapping
    public Result<Object> getComments() { return Result.success(null); }
    @PostMapping
    public Result<Void> addComment() { return Result.success(null); }
}