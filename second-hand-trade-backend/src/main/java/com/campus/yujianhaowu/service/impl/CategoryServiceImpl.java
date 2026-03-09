package com.campus.yujianhaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.yujianhaowu.mapper.CategoryMapper;
import com.campus.yujianhaowu.model.entity.Category;
import com.campus.yujianhaowu.model.vo.CategoryVO;
import com.campus.yujianhaowu.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
        // 获取所有一级分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getIsActive, true)
                .isNull(Category::getParentId)
                .orderByAsc(Category::getSortOrder);

        List<Category> parentCategories = categoryMapper.selectList(wrapper);

        // 构建分类树
        return parentCategories.stream()
                .map(parent -> {
                    CategoryVO vo = convertToVO(parent);
                    // 获取子分类
                    LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
                    childWrapper.eq(Category::getIsActive, true)
                            .eq(Category::getParentId, parent.getId())
                            .orderByAsc(Category::getSortOrder);

                    List<Category> children = categoryMapper.selectList(childWrapper);
                    vo.setChildren(children.stream().map(this::convertToVO).toList());

                    return vo;
                })
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
