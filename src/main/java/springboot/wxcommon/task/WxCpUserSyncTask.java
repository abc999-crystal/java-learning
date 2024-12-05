package springboot.wxcommon.task;

import com.google.common.collect.Lists;
import springboot.wxcommon.config.WxCpConfiguration;
import springboot.wxcommon.config.WxCpProperties;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.bean.user.WxCpDeptUserResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author zdd
 */
@Component
@Slf4j
public class WxCpUserSyncTask {

    private int limit = 10000;

    private int threadHandlePeopleSize = 200;

    @Resource
    private WxCpProperties properties;

    /**
     * 200人一次  分批
     * 多线程获取人员信息
     * 部门信息
     *
     * @return
     * @throws Exception
     */
    public List<WxCpUser> getUserListByDeptId() throws Exception {
        //这个是通讯录的
        final WxCpService wxCpTXLService = WxCpConfiguration.getCpService(properties.getAppConfigs().get(1).getCorpId(), properties.getAppConfigs().get(1).getAgentId());
        //这个是oa应用的
        final WxCpService wxCpService = WxCpConfiguration.getCpService(properties.getAppConfigs().get(0).getCorpId(), properties.getAppConfigs().get(0).getAgentId());


        WxCpDeptUserResult wxCpDeptUserResult = wxCpTXLService.getUserService().getUserListId(null, limit);
        //获取所有部门下的用户id
        List<WxCpDeptUserResult.DeptUserList> deptUserList = wxCpDeptUserResult.getDeptUser();
        List<WxCpUser> wxCpUserList = new ArrayList<>();

        List<List<WxCpDeptUserResult.DeptUserList>> deptUserListList = Lists.partition(deptUserList, threadHandlePeopleSize);
        for (List<WxCpDeptUserResult.DeptUserList> deptUserLists : deptUserListList) {

            //如果想用多线程，需要处理wxCpService2.getUserService().getById()网络问题引起的数据缺失，已测试会缺少，不稳定
            for (WxCpDeptUserResult.DeptUserList userList : deptUserLists) {
                WxCpUser wxCpUser = wxCpService.getUserService().getById(userList.getUserId());
                wxCpUserList.add(wxCpUser);
                log.info("开始加人,{}", wxCpUser.getUserId());
            }
        }

        wxCpUserList = wxCpUserList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(WxCpUser::getUserId))), ArrayList::new));

        return wxCpUserList;
    }

}
