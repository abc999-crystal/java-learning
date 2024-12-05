package springboot.wxcommon.task;

import springboot.wxcommon.config.WxCpConfiguration;
import springboot.wxcommon.config.WxCpProperties;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class WxOrgSyncTask {

    @Resource
    private WxCpProperties properties;

    public List<WxCpDepart> getOrgList() {
        final String cropId = properties.getAppConfigs().get(0).getCorpId();
        final Integer agentId = properties.getAppConfigs().get(0).getAgentId();
        List<WxCpDepart> departList = new ArrayList<>();
        try {
            // 获取企微服务和部门列表
            final WxCpService wxCpService = WxCpConfiguration.getCpService(cropId, agentId);
            departList = wxCpService.getDepartmentService().list(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return departList;
    }
}
