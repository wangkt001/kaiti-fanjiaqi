package com.campus.yujianhaowu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.ResultCode;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.ProductMapper;
import com.campus.yujianhaowu.model.entity.Product;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.ProductVO;
import com.campus.yujianhaowu.service.ProductService;
import com.campus.yujianhaowu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 商品服务实现类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final UserService userService;

    @Override
    public Page<ProductVO> listProducts(Long current, Long size, Map<String, Object> params) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = buildQueryWrapper(params);

        Page<Product> productPage = productMapper.selectPage(page, wrapper);
        Page<ProductVO> voPage = new Page<>(current, size, productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    public ProductVO getProductById(Long id, Long userId) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 增加浏览量
        incrementViewCount(id);

        ProductVO vo = convertToVO(product);

        // TODO: 设置卖家信息、图片、标签、文化信息等
        // TODO: 判断是否已收藏

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(Product product, Long sellerId) {
        // 验证卖家身份
        User seller = userService.getById(sellerId);
        if (seller == null || !"seller".equals(seller.getRole())) {
            throw new BusinessException(ResultCode.USER_NOT_SELLER);
        }

        product.setSellerId(sellerId);
        product.setStatus("pending"); // 待审核
        productMapper.insert(product);

        log.info("商品创建成功，productId: {}", product.getId());
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, Product product) {
        Product existProduct = productMapper.selectById(id);
        if (existProduct == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 更新允许修改的字段
        if (StrUtil.isNotBlank(product.getName())) {
            existProduct.setName(product.getName());
        }
        if (StrUtil.isNotBlank(product.getDescription())) {
            existProduct.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            existProduct.setPrice(product.getPrice());
        }
        if (product.getOriginalPrice() != null) {
            existProduct.setOriginalPrice(product.getOriginalPrice());
        }
        if (product.getStock() != null) {
            existProduct.setStock(product.getStock());
        }
        if (product.getCategoryId() != null) {
            existProduct.setCategoryId(product.getCategoryId());
        }

        productMapper.updateById(existProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        productMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long id, String status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        product.setStatus(status);
        if ("on_sale".equals(status)) {
            product.setPublishedAt(java.time.LocalDateTime.now());
        }
        productMapper.updateById(product);
    }

    @Override
    public Page<ProductVO> listSellerProducts(Long current, Long size, Long sellerId, String status) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSellerId, sellerId);

        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> productPage = productMapper.selectPage(page, wrapper);
        Page<ProductVO> voPage = new Page<>(current, size, productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    public List<ProductVO> getRecommendGoods(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "on_sale")
                .orderByDesc(Product::getSalesCount, Product::getViewCount)
                .last("LIMIT " + (limit != null ? limit : 8));

        List<Product> products = productMapper.selectList(wrapper);
        return products.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<ProductVO> getHotGoods(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "on_sale")
                .orderByDesc(Product::getSalesCount)
                .last("LIMIT " + (limit != null ? limit : 8));

        List<Product> products = productMapper.selectList(wrapper);
        return products.stream().map(this::convertToVO).toList();
    }

    @Override
    public List<ProductVO> getNewGoods(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "on_sale")
                .orderByDesc(Product::getCreatedAt)
                .last("LIMIT " + (limit != null ? limit : 8));

        List<Product> products = productMapper.selectList(wrapper);
        return products.stream().map(this::convertToVO).toList();
    }

    @Override
    public Page<ProductVO> searchGoods(String keyword, Long current, Long size) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "on_sale")
                .and(w -> w.like(Product::getName, keyword)
                        .or()
                        .like(Product::getDescription, keyword));

        Page<Product> productPage = productMapper.selectPage(page, wrapper);
        Page<ProductVO> voPage = new Page<>(current, size, productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setViewCount(product.getViewCount() + 1);
            productMapper.updateById(product);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementSalesCount(Long id, Integer quantity) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setSalesCount(product.getSalesCount() + quantity);
            product.setStock(product.getStock() - quantity);
            productMapper.updateById(product);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Product> buildQueryWrapper(Map<String, Object> params) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "on_sale");

        if (params != null) {
            // 分类 ID
            if (params.containsKey("categoryId")) {
                wrapper.eq(Product::getCategoryId, params.get("categoryId"));
            }
            // 卖家 ID
            if (params.containsKey("sellerId")) {
                wrapper.eq(Product::getSellerId, params.get("sellerId"));
            }
            // 价格区间
            if (params.containsKey("minPrice")) {
                wrapper.ge(Product::getPrice, params.get("minPrice"));
            }
            if (params.containsKey("maxPrice")) {
                wrapper.le(Product::getPrice, params.get("maxPrice"));
            }
            // 关键词
            if (params.containsKey("keyword")) {
                String keyword = (String) params.get("keyword");
                wrapper.and(w -> w.like(Product::getName, keyword)
                        .or()
                        .like(Product::getDescription, keyword));
            }
        }

        // 排序
        String sortBy = (String) params.get("sortBy");
        if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSalesCount);
        } else if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreatedAt);
        }

        return wrapper;
    }

    /**
     * 转换为 VO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setStock(product.getStock());
        vo.setCategoryId(product.getCategoryId());
        vo.setSellerId(product.getSellerId());
        vo.setStatus(product.getStatus());
        vo.setSalesCount(product.getSalesCount());
        vo.setViewCount(product.getViewCount());
        vo.setFavoriteCount(product.getFavoriteCount());
        vo.setHasSku(product.getHasSku());
        vo.setGiftPackaging(product.getGiftPackaging());
        vo.setCustomization(product.getCustomization());
        vo.setCreatedAt(product.getCreatedAt());
        vo.setPublishedAt(product.getPublishedAt());

        // TODO: 设置卖家信息、图片、标签、文化信息等

        return vo;
    }
}
