package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
public interface ProductService {

    /**
     * 分页查询商品
     *
     * @param current 当前页
     * @param size    每页大小
     * @param params  查询参数
     * @return 商品分页数据
     */
    Page<ProductVO> listProducts(Long current, Long size, Map<String, Object> params);

    /**
     * 根据 ID 获取商品详情
     *
     * @param id     商品 ID
     * @param userId 用户 ID（用于判断是否收藏）
     * @return 商品详情
     */
    ProductVO getProductById(Long id, Long userId);

    /**
     * 创建商品
     *
     * @param product  商品信息
     * @param sellerId 卖家 ID
     * @return 商品 ID
     */
    Long createProduct(Product product, Long sellerId);

    /**
     * 更新商品
     *
     * @param id      商品 ID
     * @param product 商品信息
     */
    void updateProduct(Long id, Product product);

    /**
     * 删除商品
     *
     * @param id 商品 ID
     */
    void deleteProduct(Long id);

    /**
     * 上下架商品
     *
     * @param id     商品 ID
     * @param status 状态
     */
    void updateProductStatus(Long id, String status);

    /**
     * 获取卖家商品列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param sellerId 卖家 ID
     * @param status   商品状态
     * @return 商品分页数据
     */
    Page<ProductVO> listSellerProducts(Long current, Long size, Long sellerId, String status);

    /**
     * 获取推荐商品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    List<ProductVO> getRecommendGoods(Integer limit);

    /**
     * 获取热门商品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    List<ProductVO> getHotGoods(Integer limit);

    /**
     * 获取最新发布商品
     *
     * @param limit 数量限制
     * @return 商品列表
     */
    List<ProductVO> getNewGoods(Integer limit);

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @param current 当前页
     * @param size    每页大小
     * @return 商品分页数据
     */
    Page<ProductVO> searchGoods(String keyword, Long current, Long size);

    /**
     * 增加浏览量
     *
     * @param id 商品 ID
     */
    void incrementViewCount(Long id);

    /**
     * 增加销量
     *
     * @param id       商品 ID
     * @param quantity 数量
     */
    void incrementSalesCount(Long id, Integer quantity);

    /**
     * 获取待审核商品列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 商品分页数据
     */
    Page<ProductVO> listPendingProducts(Long current, Long size);

    /**
     * 审核商品
     *
     * @param id     商品 ID
     * @param status 审核状态（on_sale 表示通过，offline 表示拒绝）
     */
    void auditProduct(Long id, String status);
}
