package net.wanho.fitai.infra.auth.constant;

/**
 * Redis Key 常量。
 */
public final class RedisConstant {

    /** 短信验证码 */
    public static final String SMS_CODE = "sms:code:";

    /** 短信发送限制 */
    public static final String SMS_LIMIT = "sms:limit:";

    /** 秒杀库存，后缀为秒杀 SKU 主键 id */
    public static final String SECKILL_STOCK = "seckill:stock:";

    private RedisConstant() {
    }
}
