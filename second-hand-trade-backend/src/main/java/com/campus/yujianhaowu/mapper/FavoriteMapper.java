package com.campus.yujianhaowu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.yujianhaowu.model.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收藏 Mapper
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} AND target_type = 'product' AND deleted = 0 ORDER BY created_at DESC")
    List<Favorite> selectUserProductFavorites(@Param("userId") Long userId);
}
