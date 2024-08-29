package springboot.databaseToWord;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @Description 从PostgreSQL数据库中导出数据库表信息，并生成Word文档
 * @Date 2024/8/27 10:50
 * @Version V1.0.0
 * @Author zdd55
 */
public class DatabaseToWordExporter {
    public static void main(String[] args) {
        List<String> tableNames = Arrays.asList(
                "busi_data_dict",
                "busi_data_dict_tag",
                "busi_dict_publish",
                "busi_dict_business_db_metadata",
                "busi_dict_raster_metadata",
                "busi_dict_vector_db_metadata",
                "busi_dict_temp_statistics",
                "busi_style_scheme_template",
                "zz_operator",
                "system_user_role",
                "system_user",
                "system_role_menu",
                "system_role_function",
                "system_role",
                "system_org",
                "system_menu",
                "system_log",
                "system_function"
        );

        String outputFile = "tables_metadata.docx";
        ExportToWord.exportTablesInfoToWord("test243", tableNames, outputFile);
    }
}
