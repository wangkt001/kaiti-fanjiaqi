package com.campus.yujianhaowu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.yujianhaowu.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类 Mapper 接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
