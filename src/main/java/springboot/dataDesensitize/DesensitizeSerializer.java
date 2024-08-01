package springboot.dataDesensitize;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;

/**
 * @Description 脱敏序列化器，用于在序列化字符串时根据不同的脱敏类型进行数据脱敏。
 * @Date 2024/7/31 16:05
 * @Version V1.0.0
 * @Author zdd55
 */
public class DesensitizeSerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 脱敏类型，默认为DEFAULT
     */
    private DesensitizeType type;
    /**
     * 脱敏起始位置
     */
    private int startInclude;
    /**
     * 脱敏结束位置
     */
    private int endExclude;

    public DesensitizeSerializer() {
        this.type = DesensitizeType.DEFAULT;
    }


    public DesensitizeSerializer(DesensitizeType type) {
        this.type = type;
    }

    /**
     * 序列化字符串时调用，根据脱敏类型对字符串进行相应的脱敏处理。
     *
     * @param value       待序列化的字符串
     * @param gen         JSON生成器，用于写入处理后的字符串
     * @param serializers 序列化器提供者，用于获取其他序列化器
     * @throws IOException 如果序列化过程中发生I/O错误
     */
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        switch (type) {
            case CUSTOM_RULE:
                // 这里是对字符串的startInclude到endExclude字段进行隐藏处理，如果想要实现两端保留，可以考虑使用StrUtil的replace方法
                gen.writeString(StrUtil.hide(value, startInclude, endExclude));
                break;
            case PHONE:
                gen.writeString(DesensitizedUtil.mobilePhone(value));
                break;
            case EMAIL:
                gen.writeString(DesensitizedUtil.email(value));
                break;
            case ID_CARD:
                gen.writeString(DesensitizedUtil.idCardNum(value, 1, 2));
                break;
            case BANK_CARD:
                gen.writeString(DesensitizedUtil.bankCard(value));
                break;
            case ADDRESS:
                gen.writeString(DesensitizedUtil.address(value, 8));
                break;
            case CHINESE_NAME:
                gen.writeString(DesensitizedUtil.chineseName(value));
                break;
            case PASSWORD:
                gen.writeString(DesensitizedUtil.password(value));
                break;
            default:
                gen.writeString(value);
                break;
        }
    }

    /**
     * 根据上下文信息创建自定义的序列化器，用于处理带有@Desensitize注解的属性。
     *
     * @param prov     序列化器提供者，用于获取其他序列化器
     * @param property 当前属性的信息，用于获取注解和属性类型
     * @return 自定义的序列化器实例
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property != null) {
            Desensitize annotation = property.getAnnotation(Desensitize.class);
            if (annotation != null) {
                this.type = annotation.type();
                if (annotation.type() == DesensitizeType.CUSTOM_RULE) {
                    this.startInclude = annotation.startInclude();
                    this.endExclude = annotation.endExclude();
                }
            }
        }
        return this;
    }

}
