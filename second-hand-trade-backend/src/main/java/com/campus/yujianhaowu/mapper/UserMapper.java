package com.campus.yujianhaowu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.yujianhaowu.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
