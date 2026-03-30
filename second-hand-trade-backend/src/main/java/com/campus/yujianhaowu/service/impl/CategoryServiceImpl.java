package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.yujianhaowu.mapper.CategoryMapper;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.entity.Category;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.CategoryVO;
import com.campus.yujianhaowu.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<CategoryVO> listCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getIsActive, true)
                .orderByAsc(Category::getSortOrder);

        List<Category> categories = categoryMapper.selectList(wrapper);
        return categories.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 1. 获取所有有“已上架”商品的分类ID集合
        QueryWrapper<Product> productWrapper = new QueryWrapper<>();
        productWrapper.select("DISTINCT category_id")
                .eq("status", "on_sale")
                .eq("deleted", 0);
        List<Object> categoryIdsObj = productMapper.selectObjs(productWrapper);

        if (categoryIdsObj == null || categoryIdsObj.isEmpty()) {
            return new ArrayList<>(); // 如果没有上架商品，直接返回空列表
        }

        // 提取 category_id 集合
        Set<Long> activeCategoryIds = categoryIdsObj.stream()
                .filter(obj -> obj != null)
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toSet());

        // 2. 获取包含这些 ID 的所有激活分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getIsActive, true)
                .in(Category::getId, activeCategoryIds)
                .orderByAsc(Category::getSortOrder);

        List<Category> activeCategories = categoryMapper.selectList(wrapper);

        // 3. 构建分类树 (因为只查询了有商品的具体分类，如果它们是二级分类，我们需要把它们挂载到对应的一级分类下，或者直接扁平展示)
        // 根据前端需要展示的效果，通常前台左侧只展示一级大类或者直接把有商品的分类平铺。
        // 为了兼容现有的 tree 结构，我们先提取这些分类的 parentId，把对应的一级分类也查出来
        Set<Long> parentIds = activeCategories.stream()
                .map(Category::getParentId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // 把没有父节点的（本身就是一级分类）的 ID 也加进去
        activeCategories.stream()
                .filter(c -> c.getParentId() == null)
                .forEach(c -> parentIds.add(c.getId()));

        if (parentIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询所有涉及的一级分类
        LambdaQueryWrapper<Category> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(Category::getIsActive, true)
                .isNull(Category::getParentId)
                .in(Category::getId, parentIds)
                .orderByAsc(Category::getSortOrder);

        List<Category> parentCategories = categoryMapper.selectList(parentWrapper);

        // 4. 构建树形结构，只把有商品的子分类放进去
        return parentCategories.stream()
                .map(parent -> {
                    CategoryVO vo = convertToVO(parent);

                    // 找出属于当前一级分类，且在 activeCategories 中的子分类
                    List<CategoryVO> children = activeCategories.stream()
                            .filter(c -> parent.getId().equals(c.getParentId()))
                            .map(this::convertToVO)
                            .toList();

                    vo.setChildren(children);
                    return vo;
                })
                // 过滤掉既没有子分类（子分类没商品），自己也没有商品的空的一级分类
                .filter(vo -> (vo.getChildren() != null && !vo.getChildren().isEmpty())
                        || activeCategoryIds.contains(vo.getId()))
                .toList();
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return convertToVO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(Category category) {
        categoryMapper.insert(category);
        log.info("分类创建成功，categoryId: {}", category.getId());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, Category category) {
        Category existCategory = categoryMapper.selectById(id);
        if (existCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        if (category.getName() != null) {
            existCategory.setName(category.getName());
        }
        if (category.getParentId() != null) {
            existCategory.setParentId(category.getParentId());
        }
        if (category.getDescription() != null) {
            existCategory.setDescription(category.getDescription());
        }
        if (category.getIconUrl() != null) {
            existCategory.setIconUrl(category.getIconUrl());
        }
        if (category.getSortOrder() != null) {
            existCategory.setSortOrder(category.getSortOrder());
        }
        if (category.getLevel() != null) {
            existCategory.setLevel(category.getLevel());
        }
        if (category.getIsActive() != null) {
            existCategory.setIsActive(category.getIsActive());
        }

        categoryMapper.updateById(existCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    /**
     * 转换为 VO
     */
    private CategoryVO convertToVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setParentId(category.getParentId());
        vo.setDescription(category.getDescription());
        vo.setIconUrl(category.getIconUrl());
        vo.setSortOrder(category.getSortOrder());
        vo.setLevel(category.getLevel());
        vo.setIsActive(category.getIsActive());
        return vo;
    }
}
