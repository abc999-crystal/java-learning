package springboot.giteeAPI;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @Auther zdd
 * @Date 2024/08/30 13:19
 * @Version v1.0.0
 * @Description 通过Gitee API 获取指定用户下的所有 issue，并将它们插入到数据库中
 */
public class GiteeIssueFetcher {

//    private static final String JDBC_URL = "jdbc:postgresql://192.168.110.130:45432/zzoa";
    private static final String JDBC_URL = "jdbc:postgresql://192.168.110.10:5432/zzoa";
    private static final String JDBC_USER = "sophon_oa";
    private static final String JDBC_PASSWORD = "ZZoa@123#";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    public static void main(String[] args) {
        List<AccessTokenInfo> accessTokens = getAllAccessTokens();
        for (AccessTokenInfo tokenInfo : accessTokens) {
            JSONArray issues = fetchIssuesFromGitee(tokenInfo.getAccessToken(), tokenInfo.getAssigneeId());
            if (issues != null) {
                insertIssuesIntoDatabase(issues);
            }
        }
    }


    private static List<AccessTokenInfo> getAllAccessTokens() {
        List<AccessTokenInfo> accessTokens = new ArrayList<>();
        String query = "SELECT access_token, assignee_id FROM gitee_oauth_token";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String accessToken = rs.getString("access_token");
                String assigneeId = rs.getString("assignee_id");
                accessTokens.add(new AccessTokenInfo(accessToken, assigneeId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accessTokens;
    }

    private static JSONArray fetchIssuesFromGitee(String accessToken, String assigneeId) {
        String urlString = "https://api.gitee.com/enterprises/4856171/issues?access_token=" + accessToken + "&assignee_id=" + assigneeId
//                + "&issue_state_ids=315716,315717,315724,315722,315721"
                + "&only_related_me=1&issue_type_id=81875";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 401) {
                System.out.println("accessToken跳过：" + accessToken);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            conn.disconnect();

            JSONObject obj = JSONUtil.parseObj(sb.toString());


            return JSONUtil.parseArray(obj.get("data"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CREATE TABLE test3 (
     *     id VARCHAR PRIMARY KEY,
     *     title VARCHAR,
     * 	   project_id BIGINT,
     *     program_id BIGINT,
     *     issue_state_id BIGINT,
     *     plan_started_at TIMESTAMP,
     *     deadline TIMESTAMP,
     *     duration BIGINT,
     * 	   created_at TIMESTAMP
     * );
     */

    public static void insertIssuesIntoDatabase(JSONArray issues) {
        String insertQuery = "INSERT INTO test3 (id, program_id,project_id, issue_state_id, plan_started_at, deadline, duration,created_at,title) " +
                "VALUES (?, ?, ?, ?, ?, ?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            for (Object obj : issues) {
                JSONObject issue = (JSONObject) obj;

                long id = issue.getLong("id");
                long programId = issue.getLong("program_id");
                long projectId = issue.getLong("project_id");
                long issueStateId = issue.getLong("issue_state_id");
                String planStartedAtStr = issue.getStr("plan_started_at");
                String deadlineStr = issue.getStr("deadline");
                String createdAtStr = issue.getStr("created_at");
                String title = issue.getStr("title");
                pstmt.setLong(1, id);
                pstmt.setLong(2, programId);
                pstmt.setLong(3, projectId);
                pstmt.setLong(4, issueStateId);
                if(planStartedAtStr != null && deadlineStr != null){
                    LocalDateTime planStartedAt = LocalDateTime.parse(planStartedAtStr, formatter);
                    LocalDateTime deadline = LocalDateTime.parse(deadlineStr, formatter);
                    Duration duration = Duration.between(planStartedAt, deadline);
                    pstmt.setTimestamp(5, Timestamp.valueOf(planStartedAt));
                    pstmt.setTimestamp(6, Timestamp.valueOf(deadline));
                    pstmt.setLong(7, duration.getSeconds());
                } else  {
                    pstmt.setTimestamp(5, null);
                    pstmt.setTimestamp(6, null);
                    pstmt.setLong(7, 0L);
                }
                LocalDateTime created_at = LocalDateTime.parse(createdAtStr, formatter);
                pstmt.setTimestamp(8, Timestamp.valueOf(created_at));
                pstmt.setString(9, title);


                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
