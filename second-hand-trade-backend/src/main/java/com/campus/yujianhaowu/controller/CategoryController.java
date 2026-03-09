package com.campus.yujianhaowu.controller;

import com.campus.yujianhaowu.common.Result;
import com.campus.yujianhaowu.model.entity.Category;
import com.campus.yujianhaowu.model.vo.CategoryVO;
import com.campus.yujianhaowu.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "商品分类管理", description = "商品分类 CRUD、查询等接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取分类列表")
    public Result<List<CategoryVO>> listCategories() {
        return Result.success(categoryService.listCategories());
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树")
    public Result<List<CategoryVO>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    public Result<CategoryVO> getCategoryDetail(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "创建分类")
    public Result<Long> createCategory(@RequestBody Category category) {
        return Result.success(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    public Result<Void> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category) {
        categoryService.updateCategory(id, category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
