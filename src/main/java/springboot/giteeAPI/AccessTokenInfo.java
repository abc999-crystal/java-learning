package springboot.giteeAPI;

/**
 * @Description gitee 码云信息详情
 * @Date 2024/8/29 22:08
 * @Version V1.0.0
 * @Author zdd55
 */
public class AccessTokenInfo {
    private String accessToken;
    private String assigneeId;

    public AccessTokenInfo(String accessToken, String assigneeId) {
        this.accessToken = accessToken;
        this.assigneeId = assigneeId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAssigneeId() {
        return assigneeId;
    }
}

