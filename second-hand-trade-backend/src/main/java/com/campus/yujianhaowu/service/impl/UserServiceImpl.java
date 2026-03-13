package com.campus.yujianhaowu.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.yujianhaowu.common.ResultCode;
import com.campus.yujianhaowu.exception.BusinessException;
import com.campus.yujianhaowu.mapper.UserMapper;
import com.campus.yujianhaowu.model.dto.LoginRequest;
import com.campus.yujianhaowu.model.dto.RegisterRequest;
import com.campus.yujianhaowu.model.entity.User;
import com.campus.yujianhaowu.model.vo.UserVO;
import com.campus.yujianhaowu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> login(LoginRequest request) {
        // 查询用户
        User user = getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 更新最后登录信息
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userMapper.updateById(user);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", convertToVO(user));

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existUser = getByUsername(request.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(
                StrUtil.isNotBlank(request.getNickname()) ? request.getNickname() : "用户" + System.currentTimeMillis());
        user.setRole("buyer");
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);

        userMapper.insert(user);
        log.info("用户注册成功，userId: {}", user.getId());

        return user.getId();
    }

    @Override
    public void logout(Long userId) {
        log.info("用户登出，userId: {}", userId);
        // 简化版本，无需清理 Redis 缓存
        // TODO: 后续添加 Redis 缓存功能时，在此清理用户缓存
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        log.info("=== [getCurrentUser] userId: {}", userId);
        User user = userMapper.selectById(userId);
        log.info("查询结果：{}", user == null ? "null" : "user found: " + user.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, User user) {
        User existUser = userMapper.selectById(userId);
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新允许修改的字段
        if (StrUtil.isNotBlank(user.getNickname())) {
            existUser.setNickname(user.getNickname());
        }
        if (StrUtil.isNotBlank(user.getPhone())) {
            existUser.setPhone(user.getPhone());
        }
        if (StrUtil.isNotBlank(user.getEmail())) {
            existUser.setEmail(user.getEmail());
        }
        if (user.getGender() != null) {
            existUser.setGender(user.getGender());
        }

        userMapper.updateById(existUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public void uploadAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applySeller(Long userId, Map<String, Object> sellerInfo) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 设置卖家状态和申请信息
        user.setSellerStatus("pending");
        user.setSellerInfo(JSONUtil.toJsonStr(sellerInfo));

        // 直接将角色从 buyer 改为 seller
        if ("buyer".equals(user.getRole())) {
            user.setRole("seller");
        }

        userMapper.updateById(user);
    }

    @Override
    public Map<String, String> getSellerStatus(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        Map<String, String> result = new HashMap<>();
        result.put("status", user.getSellerStatus());
        return result;
    }

    @Override
    public Map<String, Object> getSellerApplyInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", user.getSellerStatus());

        // 解析 seller_info 字段
        String sellerInfo = user.getSellerInfo();
        if (sellerInfo != null && !sellerInfo.isEmpty()) {
            try {
                Map<String, Object> info = JSONUtil.toBean(sellerInfo, Map.class);
                result.putAll(info);
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        return result;
    }

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .or()
                .eq(User::getPhone, username)
                .or()
                .eq(User::getEmail, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Page<UserVO> listUsers(Long current, Long size, String keyword) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> userPage = userMapper.selectPage(page, wrapper);
        Page<UserVO> voPage = new Page<>(current, size, userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    public Page<UserVO> listUsers(Long current, Long size) {
        return listUsers(current, size, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        userMapper.updateById(user);

        log.info("用户状态已更新 - userId: {}, status: {}", id, status);
    }

    @Override
    public Page<UserVO> listPendingSellers(Long current, Long size) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getSellerStatus, "pending")
                .orderByDesc(User::getCreatedAt);

        Page<User> userPage = userMapper.selectPage(page, wrapper);
        Page<UserVO> voPage = new Page<>(current, size, userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream()
                .map(this::convertToVO)
                .toList());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditSeller(Long userId, String status, String remark) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!"pending".equals(user.getSellerStatus())) {
            throw new BusinessException("该用户已审核过");
        }

        // 更新卖家状态
        user.setSellerStatus(status);
        
        // 如果审核通过，将用户角色升级为 seller
        if ("approved".equals(status)) {
            user.setRole("seller");
            log.info("卖家审核通过 - userId: {}, 已升级为卖家角色", userId);
        } else if ("rejected".equals(status)) {
            log.info("卖家审核拒绝 - userId: {}, 审核意见：{}", userId, remark);
        }

        userMapper.updateById(user);
    }

    /**
     * 转换为 VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setGender(user.getGender());
        vo.setRole(user.getRole());
        vo.setSellerStatus(user.getSellerStatus());
        vo.setShopName(user.getShopName());
        vo.setShopLogo(user.getShopLogo());
        vo.setFansCount(user.getFansCount());
        vo.setFollowCount(user.getFollowCount());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setLastLoginAt(user.getLastLoginAt());
        return vo;
    }
}
