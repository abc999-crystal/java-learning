package springboot.wxcommon.task;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiIndustryDepartmentListRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiIndustryDepartmentListResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.sophon.project.oa.business.system.api.entity.SystemUser;
import com.sophon.project.oa.business.system.repository.SystemUserRepo;
import com.taobao.api.ApiException;
import com.taobao.api.internal.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author majinhui
 */
//@Component
//@EnableScheduling
@Slf4j
public class DingDingTask {

    String appKey = "dingxyd3bwnwxvacgyhg";
    String appSecret = "UCg4k85Vj2TZRwrS3jQ6sIBy1gWg1Npf2LQZlwzaFAjAgqWcTmRsxBPNNe7BKV5j";
    @Resource
    private SystemUserRepo userRepo;

    private String getAccessToken() throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest req = new OapiGettokenRequest();
        req.setAppkey(appKey);
        req.setAppsecret(appSecret);
        req.setHttpMethod("GET");
        OapiGettokenResponse rsp = client.execute(req);
        return rsp.getAccessToken();
    }

    private List<Long> getAllDept(String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
        OapiIndustryDepartmentListRequest req = new OapiIndustryDepartmentListRequest();
        req.setDeptId(1L);
        req.setSize(10L);
        req.setCursor(1L);
        req.setHttpMethod("GET");
        OapiIndustryDepartmentListResponse rsp = client.execute(req, getAccessToken());
        String deptToken = rsp.getBody();
        deptToken = JSONObject.parseObject(deptToken).get("department").toString();
        JSONArray deptJson = JSONObject.parseArray(deptToken);
        List<Long> deptIds = new ArrayList<>();
        for (Object o : deptJson) {
            JSONObject parse = JSONObject.parseObject(o.toString());
            deptIds.add(Long.parseLong(parse.get("id").toString()));
        }
        return deptIds;
    }

    public OapiV2UserListResponse getUserListByDeptId(String accessToken, Long deptId, Long cursor, Long size) throws Exception {
        try {
            if (StringUtils.isEmpty(accessToken)) {
                accessToken = getAccessToken();
            }
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
            OapiV2UserListRequest req = new OapiV2UserListRequest();
            req.setDeptId(deptId);
            req.setCursor(cursor);
            req.setSize(size);
            OapiV2UserListResponse rsp = client.execute(req, accessToken);
            return rsp;
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    @Scheduled(cron = "0 0 12,15 * * ?")
    public void updateUser() throws Exception {
        String accessToken = getAccessToken();
        Long cursor = 0L;
        Long size = 100L;
        String sex = "Unknown";
        String status = "Normal";
        List<Long> allDept = getAllDept(accessToken);
//        钉钉中的人员
        HashSet<String> userIds = new HashSet<>();
        for (Long aLong : allDept) {
            OapiV2UserListResponse userListByDeptId = getUserListByDeptId(accessToken, aLong, cursor, size);
            OapiV2UserListResponse.PageResult result = userListByDeptId.getResult();
            List<OapiV2UserListResponse.ListUserResponse> list = result.getList();
            for (OapiV2UserListResponse.ListUserResponse listUserResponse : list) {
                String phone = listUserResponse.getMobile();
                String avatar = listUserResponse.getAvatar();
                String id = listUserResponse.getUserid();
                String name = listUserResponse.getName();
                SystemUser user = userRepo.selectById(id);
                if (userIds.contains(id)) {
                    continue;
                }
                userIds.add(id);
                if (ObjectUtil.isNotNull(user)) {
                    continue;
                } else {
                    String defaultTenantId = "dingd2380f05d38c8db435c2f4657eb6378f";
                    userRepo.addUserInfo(id, phone, avatar, name, sex, status, defaultTenantId);
                    userRepo.addUserTenant(id, defaultTenantId);
                }

                List<Long> deptIdList = listUserResponse.getDeptIdList();
                for (Long bLong : deptIdList) {

                    try {
                        userRepo.addOrgInfo(id, bLong + "");
                    } catch (PSQLException e) {
                        log.error("重复数据无视,{}", e.getMessage());
                    }
                }
            }
        }
        List<SystemUser> userList = userRepo.selectList(null);
//        oa库中的用户id
        List<String> userIdList = userList.stream().map(SystemUser::getId).collect(Collectors.toList());

//        要删除的用户id (数据库中用户不在钉钉用户中的部分)
        List<String> removeUserList = userIdList.stream().filter(item -> !userIds.contains(item)).collect(Collectors.toList());

        userRepo.deleteBatchIds(removeUserList);
    }
}
