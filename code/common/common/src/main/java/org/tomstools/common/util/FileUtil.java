/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnmappableCharacterException;

import org.tomstools.common.log.Logger;

/**
 * 文件操作工具类
 * 
 * @author lotomer
 * @date 2011-12-20
 * @time 下午03:26:24
 */
public final class FileUtil {
    private static final Logger LOG = Logger.getLogger(FileUtil.class);
    /** 文件头：UTF8 */
    public static final byte[] FILE_HEAD_UTF8 = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
    /** 文件头：UTF8 */
    public static final String FILE_HEAD_UTF8_STR = new String(FILE_HEAD_UTF8);

    /** 文件头：UTF-16/UCS-2, little endian */
    public static final byte[] FILE_HEAD_UTF16_LE = new byte[] { (byte) 0xFE, (byte) 0xFF };
    /** 文件头：UTF-16/UCS-2, little endian */
    public static final String FILE_HEAD_UTF16_LE_STR = new String(FILE_HEAD_UTF16_LE);

    /** 文件头：UTF-16/UCS-2, big endian */
    public static final byte[] FILE_HEAD_UTF16_BE = new byte[] { (byte) 0xFF, (byte) 0xFE };
    /** 文件头：UTF-16/UCS-2, big endian */
    public static final String FILE_HEAD_UTF16_BE_STR = new String(FILE_HEAD_UTF16_BE);

    /** 文件头：UTF-32/UCS-4, little endian */
    public static final byte[] FILE_HEAD_UTF32_LE = new byte[] { (byte) 0xFF, (byte) 0xFE,
            (byte) 0x00, (byte) 0x00 };
    /** 文件头：UTF-32/UCS-4, little endian */
    public static final String FILE_HEAD_UTF32_LE_STR = new String(FILE_HEAD_UTF32_LE);

    /** 文件头：UTF-32/UCS-4, big-endian */
    public static final byte[] FILE_HEAD_UTF32_BE = new byte[] { (byte) 0x00, (byte) 0x00,
            (byte) 0xFE, (byte) 0xFF };
    /** 文件头：UTF-32/UCS-4, big-endian */
    public static final String FILE_HEAD_UTF32_BE_STR = new String(FILE_HEAD_UTF32_BE);

    private FileUtil() {
    }

    /**
     * 获取文件名的扩展名
     * 
     * @param fileName 文件名
     * @return 文件名的扩展名。如果文件名为空，则范围空字符串。不会返回null
     */
    public static String getFileExt(String fileName) {
        String fileExt = "";
        if (!Utils.isEmpty(fileName)) {
            int index = fileName.lastIndexOf(".");
            if (-1 < index) {
                fileExt = fileName.substring(index + 1);
            }
        }

        return fileExt;
    }

    /**
     * 根据文件名获取文件内容
     * 
     * @param file 文件
     * @return 文件内容
     */
    public static String getFileContent(File file) {
        return getFileContent(file, Charset.defaultCharset().displayName());
    }

    /**
     * 根据文件名获取文件内容
     * 
     * @param file 文件
     * @param charsetName 字符集
     * @return 文件内容，UTF-8编码
     */
    public static String getFileContent(File file, String charsetName) {
        LOG.info("get file content. fileName:" + file.getAbsolutePath() + " charset:"
                + charsetName);

        if (file.isFile()) {
            FileInputStream inf = null;
            FileChannel inc = null;
            StringBuilder content = new StringBuilder();
            try {
                inf = new FileInputStream(file);
                inc = inf.getChannel();
                Charset charset = Charset.forName(charsetName);
                CharsetDecoder decoder = charset.newDecoder();
                InputStreamReader reader = new InputStreamReader(inf, decoder);
                char cbuf[] = new char[1024];
                int count = 0;
                while ((count = reader.read(cbuf)) > -1) {
                    content.append(cbuf, 0, count);
                }

                return content.toString();
            }
            catch (UnmappableCharacterException e) {
                LOG.error("The file's charset is not " + charsetName + ". file:"
                        + file.getAbsolutePath());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            finally {
                Utils.close(inc);
                Utils.close(inf);
            }
        } else {
            LOG.warn("The file is not exists or is not a file!" + file.getAbsolutePath());
        }

        return "";
    }

    /**
     * 获取源文件相对于基准文件的相对路径
     * 
     * @param baseFilePath 基准文件。必须是目录而不是文件。不允许为null
     * @param srcFilePath 源文件。必须是目录而不是文件。。不允许为null
     * @return 相对路径
     */
    public static String generateAbstractPath(File baseFilePath, File srcFilePath) {
        String baseFileName = getRealPath(baseFilePath);
        String srcFileName = getRealPath(srcFilePath);
        String[] baseNames = baseFileName.split("\\" + File.separator);
        String[] srcNames = srcFileName.split("\\" + File.separator);
        int baseLength = baseNames.length;
        // int srcLength = isFile ? srcNames.length - 1 : srcNames.length;
        int srcLength = srcNames.length;
        int length = baseLength < srcLength ? baseLength : srcLength;
        int index = 0;
        for (; index < length; index++) {
            if (!baseNames[index].equals(srcNames[index])) {
                break;
            }
        }
        StringBuilder abstractPath = new StringBuilder();
        for (int i = index; i < baseNames.length; i++) {
            abstractPath.append("../");
        }
        for (int i = index; i < srcNames.length; i++) {
            abstractPath.append(srcNames[i]);
            if (i != srcNames.length - 1) {
                abstractPath.append("/");
            }
        }

        return abstractPath.toString();
    }

    private static String getRealPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return file.getAbsolutePath();
        }
    }

    public static void saveFile(File file, String content, String charsetName) {
        if (null == file || Utils.isEmpty(content)) {
            return;
        }
        LOG.info("save file:" + file.getAbsolutePath() + " charset:" + charsetName);
        file.getParentFile().mkdirs();
        Charset cs;
        if (Utils.isEmpty(charsetName)) {
            cs = Charset.defaultCharset();
        } else {
            cs = Charset.forName(charsetName);
        }
        CharsetEncoder encoder = cs.newEncoder();
        FileOutputStream os = null;
        FileChannel out = null;
        try {
            os = new FileOutputStream(file);
            out = os.getChannel();
            out.write(encoder.encode(CharBuffer.wrap(content)));
        } catch (CharacterCodingException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            Utils.close(out);
            Utils.close(os);
        }
    }

    /**
     * 复制文件
     * 
     * @param destFile 源文件
     * @param srcFile 目标文件
     */
    public static void copyFile(File destFile, File srcFile) {
        if (!srcFile.exists() || null == destFile) {
            return;
        }
        if (null != destFile.getParentFile()) {
            destFile.getParentFile().mkdirs();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int buffReaded = 0;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            byte[] buff = new byte[1024];
            while ((buffReaded = fis.read(buff)) != -1) {
                fos.write(buff, 0, buffReaded);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.close(fis);
            Utils.close(fos);
        }
    }

    /**
     * 转换文件字符集 支持文件/目录的自转换（转换后替换源文件）
     * 
     * @param srcFileName 待转换文件名/目录
     * @param destFileName 转换后文件名/目录
     * @param srcCharsetName 待转换文件字符集
     * @param destCharsetName 转换后文件字符集
     * @param keepDirectStruct 如果是指定目录进行转换，转换后是否保持原目录结构
     */
    public static void convertFileCharset(String srcFileName, String destFileName,
            String srcCharsetName, String destCharsetName, boolean keepDirectStruct) {
        System.out.println("source charset:" + srcCharsetName + " dest charset:" + destCharsetName);
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);

        // 同目录下进行替换
        if (srcFile.getAbsolutePath().equals(destFile.getAbsolutePath())
                || (srcFile.isFile() && srcFile.getParentFile().getAbsolutePath().equals(
                        destFile.getAbsolutePath()))) {
            transeSelfCharset(srcFile, srcCharsetName, destCharsetName);
        }
        else {
            transeFileCharset(srcFile, destFile, srcFile, srcCharsetName, destCharsetName,
                    keepDirectStruct);
        }
    }

    private static void transeSelfCharset(File srcFile, String srcCharsetName,
            String destCharsetName) {
        if (srcFile.isFile()) {
            File tmpFile1 = new File(srcFile.getAbsolutePath() + ".tmp");
            if (tmpFile1.exists()) {
                tmpFile1.delete();
            }

            // 转换成临时文件
            doTranseFileCharset(srcFile, tmpFile1, srcCharsetName, destCharsetName);
            // 替换源文件
            if (!srcFile.delete()) {
                System.err.println("delete file failed:" + srcFile.getAbsolutePath());
            }
            if (!tmpFile1.renameTo(srcFile)) {
                System.err.println("rename failed! " + tmpFile1.getAbsolutePath() + " to "
                        + srcFile);
                return;
            }
        }
        else if (srcFile.isDirectory()) {
            for (File file : srcFile.listFiles()) {
                transeSelfCharset(file, srcCharsetName, destCharsetName);
            }
        }
    }

    private static void transeFileCharset(File srcBaseFile, File destBasePath, File srcFile,
            String srcCharsetName, String destCharsetName, boolean keepDirectStruct) {
        // 1、如果源路径是文件，则直接转换该文件
        if (srcFile.isFile()) {
            String destFileName;
            // 获取目标文件名
            if (!keepDirectStruct) {
                destFileName = destBasePath.getAbsolutePath() + File.separator + srcFile.getName();
            }
            else {
                // 获取源路径与源基础目录的相对目录，以便保持目录结构
                String absPath = generateAbstractPath(srcBaseFile, srcFile);
                destFileName = destBasePath.getAbsolutePath() + File.separator + absPath;
            }

            doTranseFileCharset(srcFile, new File(destFileName), srcCharsetName, destCharsetName);
        }
        // 2、如果源路径是目录，且目标路径也是目录，则转换源目录下的所有路径
        else if (srcFile.isDirectory()) {
            destBasePath.mkdirs();
            File[] files = srcFile.listFiles();
            for (File file : files) {
                transeFileCharset(srcBaseFile, destBasePath, file, srcCharsetName, destCharsetName,
                        keepDirectStruct);
            }
        }
    }

    private static void doTranseFileCharset(File srcFile, File destFile, String srcCharsetName,
            String destCharsetName) {
        System.out.println("convert file charset. Source file:" + srcFile.getAbsolutePath()
                + ". Dest file:" + destFile.getAbsolutePath());
        String fileContent = getFileContent(srcFile, srcCharsetName);
        if ("".equals(fileContent)) {
            copyFile(destFile, srcFile);
        }
        else {
            saveFile(destFile, fileContent, destCharsetName);
        }
    }

/**
     * 将目录下的文件合并成一个文件
     * @param outputFile    合并后的文件
     * @param srcDirectory  待合并文件所在目录
     * @throws FileNotFoundException 
     */
    public static void mergeFile(File outputFile, File srcDirectory) throws FileNotFoundException {
        if (outputFile.exists()){
            // 如果存在，并且是目录，则不进行合并操作
            if (outputFile.isDirectory()){
                return;
            }else{
                // 不是目录，则先删除该文件
                if (!outputFile.delete()){
                    LOG.warn("delete file failed: " + outputFile.getAbsolutePath());
                }
            }
        }
        
        // 开始合并操作
        OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
        writeFiles(out, srcDirectory);
        // 关闭输出流
        Utils.close(out);
    }

    private static void writeFiles(OutputStream out, File path) {
        if (path.isDirectory()){
            // 如果是目录则遍历文件
            File[] files = path.listFiles();
            for (File file:files){
                writeFiles(out,file);
            }
        }else if (path.isFile()){
            // 如果是文件，则开始写文件
            InputStream input = null;
            try {
                 input = new BufferedInputStream(new FileInputStream(path));
                 byte[] buffer = new byte[1024];
                 int len = 0;
                 while (0 < (len = input.read(buffer))){
                     out.write(buffer, 0, len);
                 }
            }
            catch (FileNotFoundException e) {
                LOG.error(e.getMessage(),e);
            }
            catch (IOException e) {
                LOG.error(e.getMessage(),e);
            }
            finally{
                Utils.close(input);
            }
        }else{
            // empty
        }
    }
	
    public static void main(String[] args) {
        // args = new String[4];
        // args[0] = "F:\\FlowAnalysisProject\\src\\";
        // args[1] = "F:\\FlowAnalysisProject\\src3\\";
        // args[2] = "gbk";
        // args[3] = "utf-8";
        if (3 < args.length) {
            int index = 0;
            String srcFile = args[index++];
            String destFile = args[index++];
            String srcCharsetName = args[index++];
            String destCharsetName = args[index++];
            boolean keepDirectStruct = true;
            if (4 < args.length) {
                if ("0".equals(args[index++])) {
                    keepDirectStruct = false;
                }
            }
            convertFileCharset(srcFile, destFile, srcCharsetName, destCharsetName, keepDirectStruct);
            System.out.println("ok");
        }
        else {
            System.err.println("Invalid arguments!");
            System.out.println("Usage:");
            System.out.println("    srcFileName destFileName srcCharsetName destCharsetName [0|1]");
        }
    }
}
