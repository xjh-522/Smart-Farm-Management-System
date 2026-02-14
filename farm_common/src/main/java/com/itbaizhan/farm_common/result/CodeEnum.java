package com.itbaizhan.farm_common.result;


import com.sun.net.httpserver.Authenticator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeEnum {
    Success(200, "操作成功"),
    System_error(500, "系统异常"),
    Service_error(601, "业务异常"),
    Userexist_error(602, "用户名已存在"),
    SYS_ROLE_EXIST(603, "角色已存在"),
    SYS_PERMISSION_EXIST(604, "权限已存在"),
    SYS_PERMISSION_NOT_EXIST(605, "权限不存在"),
    CAPTCHA_ISNULL(606, "验证码为空"),
    CAPTCHA_ERROR(607, "验证码错误"),
    UNAUTHORIZED(608, "无权限访问"),
    UserNotExist_error(609,"用户不存在"),
    Password_null_error(701,"密码不能为空" ),
    PERSONAL_PASSWORD_NOT_MATCH(702, "二次更新密码不一致"),
    Password_error(703, "密码错误"),
    WAREHOUSING_CODE_EXIST(704, "编码已存在"),
    WAREHOUSING_NAME_EXIST(705, "名称已存在"),
    WAREHOUSING_CATEGORY_HAS_ITEM(706, "该分类下有物料不允许删除"),
    WAREHOUSING_ORDER_ALL_READY(707, "该订单已入库,不允许修改"),
    WAREHOUSING_ORDER_NOT_EXIST(708, "订单不存在"),
    WAREHOUSING_ORDER_DETAIL_NOT_EXIST(709, "该入库单详情不存在"),
    WAREHOUSING_ORDER_DETAIL_ALL_READY(710, "该入库单详情已完成，不允许修改"),
    WAREHOUSING_ORDER_DETAIL_ISNULL(711,"库单详情不存在"),
    WAREHOUSING_INSUFFICIENT(712, "入库数量不足"),
    PLANT_CODE_EXIST(713, "编码已存在"),
    PLANT_NAME_EXIST(714, "农机类别已存在"),
    ;

    private final Integer code;
    private final String message;

}
