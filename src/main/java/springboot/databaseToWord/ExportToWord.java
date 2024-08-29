package springboot.databaseToWord;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * @Description TODO
 * @Date 2024/8/27 10:58
 * @Version V1.0.0
 * @Author zdd55
 */
public class ExportToWord {
    public static void exportTablesInfoToWord(String schema, List<String> tableNames, String outputFile) {
        String sql = "SELECT a.attname AS column_name, " +
                "format_type(a.atttypid, a.atttypmod) AS data_type, " +
                "col_description(a.attrelid, a.attnum) AS column_comment " +
                "FROM pg_attribute a " +
                "JOIN pg_class pgc ON pgc.oid = a.attrelid " +
                "JOIN pg_namespace nsp ON nsp.oid = pgc.relnamespace " +
                "WHERE pgc.relname = ? AND nsp.nspname = ? AND a.attnum > 0";

        try (Connection conn = PostgresMetaData.getConnection();
             XWPFDocument document = new XWPFDocument()) {

            for (String tableName : tableNames) {
                // 创建标题
                XWPFParagraph title = document.createParagraph();
                XWPFRun titleRun = title.createRun();
                titleRun.setText("Table: " + tableName);
                titleRun.setBold(true);

                // 创建表格，包含列名、数据类型和注释
                XWPFTable table = document.createTable();
                XWPFTableRow headerRow = table.getRow(0); // 创建第一行（表头）
                headerRow.getCell(0).setText("字段名");
                headerRow.addNewTableCell().setText("类型");
                headerRow.addNewTableCell().setText("注释");

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, tableName);
                    stmt.setString(2, schema);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            String columnName = rs.getString("column_name");
                            String dataType = rs.getString("data_type");
                            String columnComment = rs.getString("column_comment");

                            XWPFTableRow row = table.createRow(); // 创建新行
                            row.getCell(0).setText(columnName);
                            row.getCell(1).setText(dataType);
                            row.getCell(2).setText(columnComment != null ? columnComment : "N/A");
                        }
                    }
                }

                // 添加一个空段落分隔表格
                XWPFParagraph emptyParagraph = document.createParagraph();
                emptyParagraph.createRun().addBreak();
            }

            // Write the document to a file
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                document.write(out);
            }

            System.out.println("Document written successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
