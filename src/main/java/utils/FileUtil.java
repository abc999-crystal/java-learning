package utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;
/**
 * @Description 文件工具类
 * @Date 2024/6/13 15:28
 * @Version V1.0.0
 * @Author zdd55
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 将输入流解析到路径
     */
    public static void parseToPath(InputStream inputStream, String tmpPath) {
        File file = new File(tmpPath);
        try (OutputStream os = new FileOutputStream(file)){
            if(file.exists()){
                file.createNewFile();
            }

            int read = 0;
            byte[] bytes = new byte[1024 * 1024];
            //先读后写
            while ((read = inputStream.read(bytes)) > 0){
                byte[] wBytes = new byte[read];
                System.arraycopy(bytes, 0, wBytes, 0, read);
                os.write(wBytes);
            }
            os.flush();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切割文件名
     */
    public static String[] splitFileName(String fileName) {
        String[] fileNameSplit = fileName.split("\\.");
        if (fileNameSplit.length <= 1) {
            fileNameSplit = new String[]{fileName, "tmp"};
        }
        return fileNameSplit;
    }

    /**
     * 获取文件名后缀
     */
    public static String getSuffix(String fileName) {
        String[] strings = splitFileName(fileName);
        return strings[strings.length - 1];
    }

    /**
     * 获取文件名去除后缀
     */
    public static String getFileNameNoSuffix(String fileName) {
        String[] strings = splitFileName(fileName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    /**
     * 获取文件名
     */
    public static String getFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            return "TEMP_" + System.currentTimeMillis();
        }
        return originalFilename.replace("?", "-")
                .replace("/", "-")
                .replace("%", "-")
                .replace("\\", "-");
    }


    public static String getRandomFileName(MultipartFile file) {
        String fileName = getFileName(file);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String suffix = getSuffix(fileName);
        return uuid + "." + suffix;
    }

    public static String getRandomFileName(InputStream inputStream, String realName) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String suffix = getSuffix(realName);
        return uuid + "." + suffix;
    }

    public static String getJoinFileName(MultipartFile file, String joinStr) {
        String fileName = getFileName(file);
        String suffix = getSuffix(fileName);
        return getFileNameNoSuffix(fileName) + "-" + joinStr + "." + suffix;
    }


    /**
     * 删除文件
     */
    public static void deleteIfExist(String filePath) {
        File file = new File(filePath);
        // 删除临时文件
        if (file.exists()) {
            file.delete();
        }
    }


    public static String transferFile(MultipartFile file, String fileDir, String filePath) {
        File nFileDir = new File(fileDir);
        if (!nFileDir.exists()) {
            nFileDir.mkdirs();
        }
        // 绝对路径
        String aFilePath = fileDir + File.separator + filePath;
        File nFile = new File(aFilePath);
        try {
            file.transferTo(nFile);
            logger.info("文件转储到-> {}", aFilePath);
            return filePath;
        } catch (IOException e) {
            logger.error("文件转储失败", e);
            return "";
        }
    }


    public static String transferFile(InputStream inputStream, String fileDir, String filePath) {
        try {
            String aFilePath = fileDir + File.separator + filePath;
            FileUtils.copyInputStreamToFile(inputStream, new File(aFilePath));
            logger.info("文件转储到-> {}", aFilePath);
            return filePath;
        } catch (IOException e) {
            logger.error("文件转储失败", e);
            return "";
        }
    }


    public static String transferFile(MultipartFile file, String fileDir) {
        return transferFile(file, fileDir, getRandomFileName(file));
    }

    public static InputStream copyFileToStream(String fileDir, String filePath) {
        String aFilePath = fileDir + File.separator + filePath;
        try {
            File file = new File(aFilePath);
            if (!file.exists()) {
                return null;
            }
            return new FileInputStream(file);
        } catch (Exception e) {
            logger.error("文件获取失败", e);
            return null;
        } finally {
            deleteIfExist(aFilePath);
        }
    }

    /**
     * 合并文件流
     */
    public static InputStream mergeFileInputStream(List<InputStream> inputStreamList) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (InputStream inputStream : inputStreamList) {
            copy(inputStream, outputStream);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 合并下载文件流
     */
    public static void mergeFileInputStream(List<InputStream> inputStreamList, OutputStream os) {
        InputStream _inputStream = mergeFileInputStream(inputStreamList);
        copy(_inputStream, os);
    }


    public static void copy(InputStream is, OutputStream os) {
        if (null == is) {
            return;
        }
        try {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            is.close();
        } catch (IOException e) {
            logger.error("下载合并文件失败", e);
        }
    }

    public static void checkAndFixDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
