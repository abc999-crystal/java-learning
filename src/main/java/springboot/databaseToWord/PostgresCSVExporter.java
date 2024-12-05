package springboot.databaseToWord;

/**
 * @Description TODO
 * @Date 2024/10/12 17:01
 * @Version V1.0.0
 * @Author zdd55
 */
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgresCSVExporter {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:postgresql://192.168.111.244:5432/sophon_gis_server?currentSchema=test243";
        String username = "postgres";
        String password = "skzz@123";
        String csvFilePath = "D:\\work\\file\\t_shape_province.csv";
        String tableName = "t_shape_province";  // 要导出的表名

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Step 1: 建立 PostgreSQL 数据库的连接
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Step 2: 获取表的所有列名和列类型
            String columnQuery = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '" + tableName + "'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(columnQuery);

            List<String> columns = new ArrayList<>();
            List<String> geometryColumns = new ArrayList<>();

            // Step 3: 检测哪些列是几何类型
            while (resultSet.next()) {
                String columnName = resultSet.getString("column_name");
                String dataType = resultSet.getString("data_type");
                columns.add(columnName);

                if (dataType.equalsIgnoreCase("USER-DEFINED") || dataType.equalsIgnoreCase("geometry") || dataType.equalsIgnoreCase("geography")) {
                    geometryColumns.add(columnName);  // 记录几何类型的列
                }
            }

            // Step 4: 动态构造 SQL 查询，将几何字段用 ST_AsText() 包装
            StringBuilder queryBuilder = new StringBuilder("SELECT ");
            for (String column : columns) {
                if (geometryColumns.contains(column)) {
                    queryBuilder.append("public.ST_AsText(").append(column).append(") || ' ' || ");
                } else {
                    queryBuilder.append(column).append(", ");
                }
            }
            // 将几何字段拼接成一个列
            if (!geometryColumns.isEmpty()) {
                String geometryConcat = geometryColumns.stream()
                        .map(col -> "public.ST_AsText(" + col + ")")
                        .reduce((col1, col2) -> col1 + " || ', ' || " + col2)
                        .orElse("");

                queryBuilder.append(geometryConcat).append(" AS geometry_combined ");
            }
            // 去掉最后的逗号和空格
            queryBuilder.setLength(queryBuilder.length() - 2);
            queryBuilder.append(" FROM ").append(tableName);

            String query = queryBuilder.toString();
            System.out.println("Executing query: " + query);

            // Step 5: 执行查询并导出数据到 CSV 文件
            resultSet = statement.executeQuery(query);
            FileWriter csvWriter = new FileWriter(csvFilePath);

            // 写入 CSV 文件的表头
            for (String column : columns) {
                csvWriter.append(column);
                csvWriter.append(",");
            }
            csvWriter.append("geometry_combined");  // 加入拼接后的几何字段
            csvWriter.append("\n");

            // 写入查询结果到 CSV 文件
            while (resultSet.next()) {
                for (String column : columns) {
                    String value = resultSet.getString(column);
                    csvWriter.append(value != null ? value : "");
                    csvWriter.append(",");
                }
                String geometryValue = resultSet.getString("geometry_combined");
                csvWriter.append(geometryValue != null ? geometryValue : "");  // 写入拼接的几何字段
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();

            System.out.println("Data exported successfully to CSV file!");
            System.out.println("Data exported successfully to CSV file!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 清理资源
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

