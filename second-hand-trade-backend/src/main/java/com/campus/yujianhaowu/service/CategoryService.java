package com.campus.yujianhaowu.service;

import com.campus.yujianhaowu.model.entity.Category;
import com.campus.yujianhaowu.model.vo.CategoryVO;

import java.util.List;

/**
 * 商品分类服务接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
public interface CategoryService {

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    List<CategoryVO> listCategories();

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 根据 ID 获取分类
     *
     * @param id 分类 ID
     * @return 分类
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 分类 ID
     */
    Long createCategory(Category category);

    /**
     * 更新分类
     *
     * @param id       分类 ID
     * @param category 分类信息
     */
    void updateCategory(Long id, Category category);

    /**
     * 删除分类
     *
     * @param id 分类 ID
     */
    void deleteCategory(Long id);
}
