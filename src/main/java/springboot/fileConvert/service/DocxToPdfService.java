package springboot.fileConvert.service;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class DocxToPdfService {

    private final static Logger logger = LoggerFactory.getLogger(DocxToPdfService.class);

    @Autowired
    private OfficeToPdfService officeToPdf;

    /**
     * windows下转pdf
     * @param docxFilePath
     * @param pdfFilePath
     */
    private void word2pdf(String docxFilePath, String pdfFilePath) {
        try (InputStream docxInputStream = new FileInputStream(docxFilePath);
             OutputStream outputStream = new FileOutputStream(pdfFilePath)) {

            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream)
                    .as(DocumentType.DOCX)
                    .to(outputStream)
                    .as(DocumentType.PDF)
                    .execute();
            converter.shutDown();
        } catch (Exception e) {
            logger.warn("[documents4J] word转pdf失败: " + e);
        }
    }

    /**
     * linux下转pdf
     * @param docxFilePath
     * @param pdfFilePath
     */
    private void word2pdf2(String docxFilePath, String pdfFilePath) {
        try {
            officeToPdf.openOfficeToPDF(docxFilePath, pdfFilePath);
        } catch (OfficeException e) {
            logger.warn("抱歉，该文件版本不兼容，文件版本错误。");
        }
    }
}
