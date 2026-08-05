package net.wanho.fitai.modules.mall.code;

import net.wanho.fitai.infra.exception.ExceptionResult;

/**
 * 电商模块错误码 6001-6999
 */
public enum MallCode implements ExceptionResult {
    PRODUCT_NOT_FOUND(6001, "商品不存在"),
    SKU_NOT_FOUND(6002, "SKU不存在"),
    STOCK_NOT_ENOUGH(6003, "库存不足"),
    ORDER_NOT_FOUND(6004, "订单不存在"),
    ORDER_STATUS_ERROR(6005, "订单状态异常"),
    CART_EMPTY(6006, "购物车为空"),
    SECKILL_NOT_START(6007, "活动未开始"),
    SECKILL_ENDED(6008, "活动已结束"),
    SECKILL_SOLD_OUT(6009, "已售罄"),
    SECKILL_ALREADY_BOUGHT(6010, "您已参与过此活动"),
    ORDER_EXPIRED(6011, "订单已超时"),
    NOT_FOUND(6012, "资源不存在"),
    /** 已开启商城 ES 时，C 端搜索仅走 ES；ES 不可用或查询失败时返回，不回落 MySQL */
    PRODUCT_SEARCH_UNAVAILABLE(6013, "商品搜索服务暂不可用，请稍后重试");

    private final int code;
    private final String msg;

    MallCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String msg() {
        return this.msg;
    }
}
