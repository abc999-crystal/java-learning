package springboot.databaseToWord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * @Description 获取列名、数据类型和注释
 * @Date 2024/8/27 10:58
 * @Version V1.0.0
 * @Author zdd55
 */
public class TableMetaData {
    public static void getTableColumnsInfo(String schema, String tableName) {
        String sql = "SELECT a.attname AS column_name, " +
                "format_type(a.atttypid, a.atttypmod) AS data_type, " +
                "col_description(a.attrelid, a.attnum) AS column_comment " +
                "FROM pg_attribute a " +
                "JOIN pg_class pgc ON pgc.oid = a.attrelid " +
                "JOIN pg_namespace nsp ON nsp.oid = pgc.relnamespace " +
                "WHERE pgc.relname = ? AND nsp.nspname = ? AND a.attnum > 0";

        try (Connection conn = PostgresMetaData.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableName);
            stmt.setString(2, schema);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("column_name");
                    String dataType = rs.getString("data_type");
                    String columnComment = rs.getString("column_comment");

                    System.out.println("Column: " + columnName);
                    System.out.println("Type: " + dataType);
                    System.out.println("Comment: " + columnComment);
                    System.out.println("------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
