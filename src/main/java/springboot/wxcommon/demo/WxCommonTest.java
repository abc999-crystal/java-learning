package springboot.wxcommon.demo;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springboot.wxcommon.config.WxCpConfiguration;
import springboot.wxcommon.config.WxCpProperties;
import springboot.wxcommon.service.WxMessageService;

import javax.annotation.Resource;

@SpringBootApplication
public class WxCommonTest {

    @Resource
    private static WxCpProperties properties;

    public static void main(String[] args) {
        // 发送消息
        WxMessageService service = new WxMessageService();
        service.sendWxTextMsgAsync("测试消息", "zdd55");

        Integer agentId = properties.getAppConfigs().get(0).getAgentId();
        String cropId = properties.getAppConfigs().get(0).getCorpId();
        WxCpService wxCpService = WxCpConfiguration.getCpService(cropId, agentId);
        try {
            String accessToken = wxCpService.getAccessToken();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
