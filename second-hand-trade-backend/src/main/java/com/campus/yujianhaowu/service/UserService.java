package com.campus.yujianhaowu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.model.dto.LoginRequest;
import com.campus.yujianhaowu.model.dto.RegisterRequest;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.UserVO;

import java.util.Map;

/**
 * 用户服务接口
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return Token 和用户信息
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户 ID
     */
    Long register(RegisterRequest request);

    /**
     * 用户登出
     *
     * @param userId 用户 ID
     */
    void logout(Long userId);

    /**
     * 获取当前用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId 用户 ID
     * @param user   用户信息
     */
    void updateProfile(Long userId, User user);

    /**
     * 修改密码
     *
     * @param userId      用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 上传头像
     *
     * @param userId    用户 ID
     * @param avatarUrl 头像 URL
     */
    void uploadAvatar(Long userId, String avatarUrl);

    /**
     * 申请成为卖家
     *
     * @param userId     用户 ID
     * @param sellerInfo 卖家资质信息
     */
    void applySeller(Long userId, Map<String, Object> sellerInfo);

    /**
     * 获取卖家状态
     *
     * @param userId 用户 ID
     * @return 卖家状态
     */
    Map<String, String> getSellerStatus(Long userId);

    /**
     * 获取卖家申请信息
     *
     * @param userId 用户 ID
     * @return 卖家申请信息
     */
    Map<String, Object> getSellerApplyInfo(Long userId);

    /**
     * 根据 ID 获取用户
     *
     * @param userId 用户 ID
     * @return 用户
     */
    User getById(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getByUsername(String username);

    /**
     * 分页查询用户
     *
     * @param current 当前页
     * @param size    每页大小
     * @param keyword 关键词
     * @return 用户分页数据
     */
    Page<UserVO> listUsers(Long current, Long size, String keyword);

    /**
     * 分页查询用户（管理员）
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 用户分页数据
     */
    Page<UserVO> listUsers(Long current, Long size);

    /**
     * 更新用户状态（管理员）
     *
     * @param id     用户 ID
     * @param status 状态（1-正常 0-禁用）
     */
    void updateUserStatus(Long id, Integer status);
}
