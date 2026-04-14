package com.campus.yujianhaowu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

import java.time.LocalDateTime;
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
        log.info("=== [listProducts] 查询商品列表 ===");
        log.info("current: {}, size: {}, params: {}", current, size, params);

        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = buildQueryWrapper(params);

        log.info("查询条件：status='on_sale'");

        Page<Product> productPage = productMapper.selectPage(page, wrapper);
        log.info("查询结果：total={}, records={}", productPage.getTotal(), productPage.getRecords().size());

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

        if (seller == null) {
            log.warn("用户不存在，sellerId: {}", sellerId);
            throw new BusinessException(ResultCode.USER_NOT_SELLER);
        }

        log.info("创建商品 - userId: {}, username: {}, role: {}",
                seller.getId(), seller.getUsername(), seller.getRole());

        if (!"seller".equals(seller.getRole())) {
            log.warn("用户不是卖家 - userId: {}, username: {}, role: {}",
                    seller.getId(), seller.getUsername(), seller.getRole());
            throw new BusinessException(ResultCode.USER_NOT_SELLER);
        }

        // 处理 Base64 图片（如果是 Base64 格式，直接存储）
        if (StrUtil.isNotBlank(product.getImageUrl()) && product.getImageUrl().startsWith("data:image")) {
            log.info("检测到 Base64 图片，直接存储");
        }

        product.setSellerId(sellerId);
        product.setStatus("on_sale"); // 直接上架
        product.setPublishedAt(LocalDateTime.now());
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
        
        // 处理图片更新 - 支持空字符串清除图片
        if (product.getImageUrl() != null) {
            if (StrUtil.isNotBlank(product.getImageUrl())) {
                // 如果是 Base64 格式，直接存储
                if (product.getImageUrl().startsWith("data:image")) {
                    log.info("更新商品图片 - 检测到 Base64 格式");
                }
                existProduct.setImageUrl(product.getImageUrl());
            } else {
                // 空字符串表示清除图片
                existProduct.setImageUrl(null);
            }
        }

        productMapper.updateById(existProduct);
        log.info("商品更新成功，productId: {}", id);
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
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                .setSql("view_count = COALESCE(view_count, 0) + 1");
        productMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementSalesCount(Long id, Integer quantity) {
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                .setSql("sales_count = COALESCE(sales_count, 0) + " + quantity)
                .setSql("stock = stock - " + quantity);
        productMapper.update(null, updateWrapper);
    }

    @Override
    public Page<ProductVO> listPendingProducts(Long current, Long size) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "pending")
                .orderByDesc(Product::getCreatedAt);

        Page<Product> productPage = productMapper.selectPage(page, wrapper);
        Page<ProductVO> voPage = new Page<>(current, size, productPage.getTotal());
        voPage.setRecords(productPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditProduct(Long id, String status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
        }

        if (!"pending".equals(product.getStatus())) {
            throw new BusinessException("该商品状态不是待审核状态");
        }

        product.setStatus(status);
        if ("on_sale".equals(status)) {
            product.setPublishedAt(LocalDateTime.now());
        }
        productMapper.updateById(product);

        log.info("商品审核完成 - productId: {}, status: {}", id, status);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Product> buildQueryWrapper(Map<String, Object> params) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 临时修改：查询所有状态的商品用于测试
        // wrapper.eq(Product::getStatus, "on_sale");
        log.info("构建查询条件：status 过滤已移除（测试用）");

        if (params != null) {
            // 分类 ID
            if (params.get("categoryId") != null) {
                wrapper.eq(Product::getCategoryId, params.get("categoryId"));
            }
            // 卖家 ID
            if (params.get("sellerId") != null) {
                wrapper.eq(Product::getSellerId, params.get("sellerId"));
            }
            // 价格区间
            if (params.get("minPrice") != null) {
                wrapper.ge(Product::getPrice, params.get("minPrice"));
            }
            if (params.get("maxPrice") != null) {
                wrapper.le(Product::getPrice, params.get("maxPrice"));
            }
            // 关键词
            String keyword = (String) params.get("keyword");
            if (StrUtil.isNotBlank(keyword)) {
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
        vo.setImageUrl(product.getImageUrl());

        // TODO: 设置卖家信息、标签、文化信息等

        return vo;
    }
}
