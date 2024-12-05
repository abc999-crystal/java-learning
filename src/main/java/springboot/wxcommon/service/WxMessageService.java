package springboot.wxcommon.service;

import springboot.wxcommon.config.WxCpConfiguration;
import springboot.wxcommon.config.WxCpProperties;
import springboot.wxcommon.utils.WxMsgUtil;
import me.chanjar.weixin.cp.api.WxCpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步发送企业微信消息
 */
@Service
public class WxMessageService {

    private static final Logger logger = LoggerFactory.getLogger(WxMessageService.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Resource
    private WxCpProperties properties;

    public void sendWxTextMsgAsync(String msg, String toUserIds) {
        executorService.submit(() -> {
            String corpId = properties.getAppConfigs().get(0).getCorpId();
            Integer agentId = properties.getAppConfigs().get(0).getAgentId();
            WxCpService wxCpService = WxCpConfiguration.getCpService(corpId, agentId);
            long startTime = System.currentTimeMillis();
            try {
                WxMsgUtil.sendTextMsg(wxCpService, agentId, msg, toUserIds, null, null);
            } catch (Exception e) {
                logger.error("发送消息失败：" + msg, e);
            }
            long endTime = System.currentTimeMillis();
            logger.info("发送消息：" + msg + "成功；耗时时长：" + (endTime - startTime) + " ms");
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

