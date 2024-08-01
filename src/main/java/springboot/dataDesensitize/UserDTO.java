package springboot.dataDesensitize;

import lombok.Data;

@Data
public class UserDTO {

    /**
     * 用户姓名
     */
    @Desensitize(type = DesensitizeType.CHINESE_NAME)
    private String name;

    /**
     * 用户手机号
     */
    @Desensitize(type = DesensitizeType.PHONE)
    private String phoneNumber;

    /**
     * 用户电子邮件地址
     */
    @Desensitize(type = DesensitizeType.EMAIL)
    private String email;

    /**
     * 用户密码
     */
    @Desensitize(type = DesensitizeType.PASSWORD)
    private String password;

    /**
     * 用户身份证号码
     */
    @Desensitize(type = DesensitizeType.ID_CARD)
    private String idCard;

    /**
     * 用户银行卡号
     */
    @Desensitize(type = DesensitizeType.BANK_CARD)
    private String bankCard;

    /**
     * 用户地址
     */
    @Desensitize(type = DesensitizeType.ADDRESS)
    private String address;

    /**
     * 游戏名称
     */
    @Desensitize(type = DesensitizeType.CUSTOM_RULE, startInclude = 2, endExclude = 6)
    private String gameName;
}

