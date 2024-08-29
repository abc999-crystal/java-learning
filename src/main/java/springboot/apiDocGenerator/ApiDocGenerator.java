package springboot.apiDocGenerator;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Description APIFOX根据类生成请求参数
 * @Date 2024/8/27 15:48
 * @Version V1.0.0
 * @Author zdd55
 */
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ApiDocGenerator {

    public static void main(String[] args) throws Exception {
        generateApiDocToWord("src/main/java/springboot/apiDocGenerator/Request.java", "output.docx");
    }

    public static void generateApiDocToWord(String sourceFilePath, String outputFilePath) throws Exception {
        // 解析 Java 文件
        CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(sourceFilePath));

        // 创建一个新的 Word 文档
        XWPFDocument document = new XWPFDocument();

        // 创建标题
        XWPFParagraph title = document.createParagraph();
        title.createRun().setText("API 请求参数文档");

        // 创建表格
        XWPFTable table = document.createTable();

        // 创建表头行
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("参数");
        headerRow.addNewTableCell().setText("类型");
        headerRow.addNewTableCell().setText("备注");

        // 遍历字段并提取注释和类型
        cu.findAll(FieldDeclaration.class).forEach(field -> {
            String paramName = field.getVariables().get(0).getNameAsString();
            String paramType = field.getCommonType().asString();

            // 提取 Javadoc 注释中的文本
            String remark = field.getJavadoc().isPresent()
                    ? field.getJavadoc().get().getDescription().toText().trim()
                    : "无";

            // 创建新行
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(paramName);
            row.getCell(1).setText(paramType);
            row.getCell(2).setText(remark);
        });

        // 将文档保存到文件
        try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
            document.write(out);
        }

        // 关闭文档
        document.close();

        System.out.println("API 文档生成成功，文件路径: " + outputFilePath);
    }
}