package springboot.dataDesensitize;

/**
 * @Description 脱敏类型枚举类
 * @Date 2024/7/31 16:00
 * @Version V1.0.0
 * @Author zdd55
 */
public enum DesensitizeType {
    /**
     * 默认脱敏
     */
    DEFAULT,
    /**
     * 自定义脱敏
     */
    CUSTOM_RULE,
    /**
     * 手机号脱敏
     */
    PHONE,
    /**
     * 电子邮件脱敏
     */
    EMAIL,
    /**
     * 身份证号脱敏
     */
    ID_CARD,
    /**
     * 银行卡号脱敏
     */
    BANK_CARD,
    /**
     * 地址脱敏
     */
    ADDRESS,
    /**
     * 中文姓名脱敏
     */
    CHINESE_NAME,
    /**
     * 密码脱敏
     */
    PASSWORD;
}
