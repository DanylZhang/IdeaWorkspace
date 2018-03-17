import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.PrimitiveIterator;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * ZIP工具包(支持中文)
 * 依赖：ant-1.9.6.jar
 */
public class ZipUtils {

    /**
     * 使用GBK编码可以避免压缩中文文件名乱码
     */
    private static final String CHINESE_CHARSET = "GBK";
    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * 压缩文件
     *
     * @param sourceFolder 压缩文件夹
     * @param zipFilePath  压缩文件输出路径
     */
    public static void zip(String sourceFolder, String zipFilePath) {
        OutputStream os = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;
        try {
            os = new FileOutputStream(zipFilePath);
            bos = new BufferedOutputStream(os);
            zos = new ZipOutputStream(bos);
            // 解决中文文件名乱码
            zos.setEncoding(CHINESE_CHARSET);
            File file = new File(sourceFolder);
            String basePath = null;
            if (file.isDirectory()) {//压缩文件夹
                basePath = file.getPath();
            } else {
                basePath = file.getParent();
            }
            zipFile(file, basePath, zos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 递归压缩文件
     *
     * @param parentFile
     * @param basePath
     * @param zos
     * @throws Exception
     */
    private static void zipFile(File parentFile, String basePath, ZipOutputStream zos) throws Exception {
        File[] files = new File[0];
        if (parentFile.isDirectory()) {
            files = parentFile.listFiles();
        } else {
            files = new File[1];
            files[0] = parentFile;
        }
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[CACHE_SIZE];
        for (File file : files) {
            if (file.isDirectory()) {
                pathName = file.getPath().substring(basePath.length() + 1) + File.separator;
                zos.putNextEntry(new ZipEntry(pathName));
                zipFile(file, basePath, zos);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                zos.putNextEntry(new ZipEntry(pathName));
                int nRead = 0;
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    zos.write(cache, 0, nRead);
                }
                bis.close();
                is.close();
            }
        }
    }

    /**
     * 解压压缩包
     *
     * @param zipFilePath 压缩文件路径
     * @param destDir     解压目录
     */
    public static void unZip(String zipFilePath, String destDir) {
        ZipFile zipFile = null;
        try {
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            zipFile = new ZipFile(zipFilePath, CHINESE_CHARSET);
            Enumeration<ZipEntry> zipEntries = zipFile.getEntries();
            File file, parentFile;
            ZipEntry entry;
            byte[] cache = new byte[CACHE_SIZE];
            while (zipEntries.hasMoreElements()) {
                entry = (ZipEntry) zipEntries.nextElement();
                if (entry.isDirectory()) {
                    new File(destDir + entry.getName()).mkdirs();
                    continue;
                }
                bis = new BufferedInputStream(zipFile.getInputStream(entry));
                file = new File(destDir + entry.getName());
                parentFile = file.getParentFile();
                if (parentFile != null && (!parentFile.exists())) {
                    parentFile.mkdirs();
                }
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, CACHE_SIZE);
                int readIndex = 0;
                while ((readIndex = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    fos.write(cache, 0, readIndex);
                }
                bos.flush();
                bos.close();
                fos.close();
                bis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void unrar(String sourceRar, String destDir) throws Exception {
        Archive a = null;
        FileOutputStream fos = null;
        try {
            a = new Archive(new File(sourceRar));
            System.out.println(a.nextFileHeader().getFileNameString());
            System.exit(0);
            FileHeader fh = a.nextFileHeader();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    //1 根据不同的操作系统拿到相应的 destDirName 和 destFileName
                    String compressFileName = fh.getFileNameString().trim();
                    String destFileName = "";
                    String destDirName = "";
                    //非windows系统
                    if (File.separator.equals("/")) {
                        destFileName = destDir + compressFileName.replaceAll("\\\\", "/");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
                        //windows系统
                    } else {
                        destFileName = destDir + compressFileName.replaceAll("/", "\\\\");
                        destDirName = destFileName.substring(0, destFileName.lastIndexOf("\\"));
                    }
                    //2创建文件夹
                    File dir = new File(destDirName);
                    if (!dir.exists() || !dir.isDirectory()) {
                        dir.mkdirs();
                    }
                    //3解压缩文件
                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        } catch (Exception e) {
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (a != null) {
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void unrarBy7z(String sourceRar, String destDir) throws Exception {
        try {
            String cmd = "cmd /c \"c:\\Program Files\\7-Zip\\7z.exe\" e -y " + sourceRar + " -o" + destDir;
            System.out.println(cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//        String sourceFolder = "D:/test/中文名.txt";
//        String zipFilePath = "D:/test/zip/压缩文件.zip";
//        ZipUtils.zip(sourceFolder, zipFilePath);

        String sourcePath = "D:/baiduYun/java并发教程";
        String destDir = "D:/test/zip/";
        File filePath = new File(sourcePath);

        File[] files = filePath.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            System.out.println(file);
            System.out.println(FilenameUtils.getBaseName(file.toString()));
            String filename = FilenameUtils.getBaseName(file.toString());

            ZipUtils.unrarBy7z(file.toString(), destDir);

            File destFile = new File(destDir+(i+1)+".avi");
            destFile.renameTo(new File(destDir+filename+".avi"));
        }
        System.out.println("********执行成功**********");
    }
}