package com.park.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author park-team
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误
    FAILURE(400, "操作失败"),
    UNAUTHORIZED(401, "未认证，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    PARAM_ERROR(406, "参数校验失败"),

    // 服务器错误
    SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误码 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_EXISTS(1004, "用户已存在"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    TOKEN_INVALID(1006, "Token无效"),
    DATA_NOT_FOUND(1007, "数据不存在"),
    DATA_EXISTS(1008, "数据已存在"),
    DATA_SAVE_ERROR(1009, "数据保存失败"),
    DATA_UPDATE_ERROR(1010, "数据更新失败"),
    DATA_DELETE_ERROR(1011, "数据删除失败"),
    UPLOAD_ERROR(1012, "文件上传失败"),
    FILE_NOT_FOUND(1013, "文件不存在"),
    REQUEST_TOO_FREQUENT(1014, "请求过于频繁，请稍后再试"),

    // 评价审核业务错误码 2xxx
    EVALUATION_NOT_FOUND(2001, "评价记录不存在"),
    EVALUATION_STATUS_ERROR(2002, "评价记录状态不允许此操作"),
    EVALUATION_EXISTS(2003, "该园区该年度季度的评价记录已存在"),
    AUDIT_PERMISSION_DENIED(2004, "无审核权限"),
    AUDIT_STATUS_ERROR(2005, "当前状态不允许审核");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 消息
     */
    private final String message;
}
