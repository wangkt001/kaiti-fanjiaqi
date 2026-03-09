package com.campus.yujianhaowu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.PageResult;
import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品 CRUD、查询等接口")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "获取商品列表")
    public Result<PageResult<ProductVO>> listProducts(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy) {

        Map<String, Object> params = new HashMap<>();
        params.put("categoryId", categoryId);
        params.put("sellerId", sellerId);
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("keyword", keyword);
        params.put("sortBy", sortBy);

        Page<ProductVO> page = productService.listProducts(current, size, params);
        PageResult<ProductVO> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());

        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public Result<ProductVO> getProductDetail(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        return Result.success(productService.getProductById(id, userId));
    }

    @PostMapping
    @Operation(summary = "创建商品")
    public Result<Long> createProduct(@RequestBody Product product, HttpServletRequest request) {
        Long sellerId = (Long) request.getAttribute("userId");
        return Result.success(productService.createProduct(product, sellerId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品")
    public Result<Void> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {
        productService.updateProduct(id, product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "上下架商品")
    public Result<Void> updateProductStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        productService.updateProductStatus(id, status);
        return Result.success();
    }

    @GetMapping("/seller")
    @Operation(summary = "获取卖家商品列表")
    public Result<PageResult<ProductVO>> listSellerProducts(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {

        Long sellerId = (Long) request.getAttribute("userId");
        Page<ProductVO> page = productService.listSellerProducts(current, size, sellerId, status);
        PageResult<ProductVO> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());

        return Result.success(result);
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐商品")
    public Result<List<ProductVO>> getRecommendGoods(
            @RequestParam(required = false) Integer limit) {
        return Result.success(productService.getRecommendGoods(limit));
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门商品")
    public Result<List<ProductVO>> getHotGoods(
            @RequestParam(required = false) Integer limit) {
        return Result.success(productService.getHotGoods(limit));
    }

    @GetMapping("/new")
    @Operation(summary = "获取最新发布商品")
    public Result<List<ProductVO>> getNewGoods(
            @RequestParam(required = false) Integer limit) {
        return Result.success(productService.getNewGoods(limit));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商品")
    public Result<PageResult<ProductVO>> searchGoods(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        Page<ProductVO> page = productService.searchGoods(keyword, current, size);
        PageResult<ProductVO> result = PageResult.of(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize());

        return Result.success(result);
    }
}
