package com.campus.yujianhaowu.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author yujianhaowu
 * @since 2026-03-09
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数验证失败
     */
    VALIDATION_ERROR(400, "参数验证失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(405, "方法不允许"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(4000, "业务异常"),

    /**
     * 用户相关
     */
    USER_NOT_FOUND(4001, "用户不存在"),
    USER_PASSWORD_ERROR(4002, "用户名或密码错误"),
    USER_DISABLED(4003, "用户已被禁用"),
    USER_ALREADY_EXISTS(4004, "用户已存在"),
    USER_NOT_SELLER(4005, "当前用户不是卖家"),
    USER_SELLER_AUDIT_PENDING(4006, "卖家资质审核中"),
    USER_SELLER_AUDIT_REJECTED(4007, "卖家资质审核未通过"),

    /**
     * 商品相关
     */
    PRODUCT_NOT_FOUND(4101, "商品不存在"),
    PRODUCT_NOT_ON_SALE(4102, "商品已下架"),
    PRODUCT_OUT_OF_STOCK(4103, "商品库存不足"),
    PRODUCT_AUDIT_PENDING(4104, "商品审核中"),
    PRODUCT_AUDIT_REJECTED(4105, "商品审核未通过"),

    /**
     * 订单相关
     */
    ORDER_NOT_FOUND(4201, "订单不存在"),
    ORDER_STATUS_ERROR(4202, "订单状态错误"),
    ORDER_CANCELLED(4203, "订单已取消"),
    ORDER_NOT_PAID(4204, "订单未支付"),

    /**
     * 文件相关
     */
    FILE_UPLOAD_ERROR(4301, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(4302, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(4303, "文件大小超出限制"),

    /**
     * 评论相关
     */
    REVIEW_NOT_FOUND(4401, "评论不存在"),
    REVIEW_ALREADY_EXISTS(4402, "已发表过评论"),
    REVIEW_NOT_ALLOWED(4403, "当前订单不允许评论"),

    /**
     * 收藏相关
     */
    FAVORITE_ALREADY_EXISTS(4501, "已收藏"),
    FAVORITE_NOT_FOUND(4502, "收藏不存在"),

    /**
     * 点赞相关
     */
    LIKE_ALREADY_EXISTS(4601, "已点赞"),
    LIKE_NOT_FOUND(4602, "点赞不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
