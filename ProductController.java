package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "获取商品列表")
    public Result<PageResult<ProductVO>> getProducts(
            @Parameter(description = "分类 ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.getProducts(categoryId, keyword, current, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public Result<ProductVO> getProductDetail(
            @Parameter(description = "商品 ID") @PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }
}