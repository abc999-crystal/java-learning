package springboot.databaseToWord;

import java.sql.Connection;
import java.sql.DriverManager;
/**
 * @Description 创建数据库连接
 * @Date 2024/8/27 10:57
 * @Version V1.0.0
 * @Author zdd55
 */
public class PostgresMetaData {
    private static final String URL = "jdbc:postgresql://192.168.111.244:5432/sophon_gis_server";
    private static final String USER = "postgres";
    private static final String PASSWORD = "skzz@123";

    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
