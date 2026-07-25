package com.smartrent.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未登录或Token已过期"),
    FORBIDDEN(1002, "无权限访问"),
    TOKEN_INVALID(1003, "Token无效"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),

    // 参数相关 2xxx
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "参数缺失"),

    // 业务相关 3xxx
    USER_EXISTS(3001, "用户名已存在"),
    PHONE_EXISTS(3002, "手机号已注册"),
    USER_NOT_FOUND(3003, "用户不存在"),
    PASSWORD_ERROR(3004, "密码错误"),
    HOUSE_NOT_FOUND(3005, "房源不存在"),
    HOUSE_OFFLINE(3006, "房源已下架"),
    APPOINTMENT_EXISTS(3007, "该时段已有预约"),
    CONTRACT_ACTIVE(3008, "该房源已有生效中的合同"),

    // 系统相关 5xxx
    SYSTEM_ERROR(5001, "系统内部错误"),
    SERVICE_UNAVAILABLE(5002, "服务不可用"),
    RATE_LIMIT(5003, "请求过于频繁");

    private final int code;
    private final String msg;
}
