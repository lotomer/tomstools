/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.lotomer.plugins.tomstools.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnmappableCharacterException;

/**
 * 文件操作工具类
 * 
 * @author lotomer
 * @date 2011-12-20
 * @time 下午03:26:24
 */
public final class FileUtil {
    // private static final Logger logger = Logger.getLogger(FileUtil.class);
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
     * 根据文件名获取文件内容
     * 
     * @param file
     *            文件
     * @return 文件内容
     */
    public static String getFileContent(File file) {
        return getFileContent(file, Charset.defaultCharset().displayName());
    }

    /**
     * 根据文件名获取文件内容
     * 
     * @param file
     *            文件
     * @param charsetName
     *            字符集
     * @return 文件内容，UTF-8编码。不会为null，可为空字符串
     */
    public static String getFileContent(File file, String charsetName) {
        if (file.isFile()) {
            RandomAccessFile inf = null;
            FileChannel inc = null;
            try {
                inf = new RandomAccessFile(file, "r");
                long inputLength = file.length();
                inc = inf.getChannel();
                MappedByteBuffer inputData = inc.map(FileChannel.MapMode.READ_ONLY, 0, inputLength);
                Charset charset = Charset.forName(charsetName);
                CharsetDecoder decoder = charset.newDecoder();
                CharBuffer cb = decoder.decode(inputData);
                inputData.clear();
                inputData = null;

                return cb.toString();
            }
            catch (UnmappableCharacterException e) {
                System.err.println("The file's charset is not " + charsetName + ". file:"
                        + file.getAbsolutePath());
                //e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                close(inc);
                close(inf);
                System.gc();// 必须GC才能释放关联的文件句柄
            }
        } else {
            // logger.warn("The file is not exists or is not a file!" + file.getAbsolutePath());
        }

        return "";
    }

    /**
     * 获取源文件相对于基准文件的相对路径
     * 
     * @param baseFilePath
     *            基准文件。必须是目录而不是文件。不允许为null
     * @param srcFilePath
     *            源文件。必须是目录而不是文件。。不允许为null
     * @return 相对路径
     */
    public static String generateAbstractPath(File baseFilePath, File srcFilePath) {
        String baseFileName = getRealPath(baseFilePath);
        String srcFileName = getRealPath(srcFilePath);
        String[] baseNames = baseFileName.split("\\" + File.separator);
        String[] srcNames = srcFileName.split("\\" + File.separator);
        int baseLength = baseNames.length;
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
        }
        catch (IOException e) {
            // logger.error(e.getMessage());
            return file.getAbsolutePath();
        }
    }

    public static void saveFile(File file, String content, String charsetName) {
        // if (Utils.isEmpty(fileName) || Utils.isEmpty(content)) {
        // return;
        // }
        // logger.info("save file:" + fileName + " charset:" + charsetName);
        file.getParentFile().mkdirs();
        Charset cs;
        if (null == charsetName || "".equals(charsetName)) {
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
        }
        catch (CharacterCodingException e) {
            // logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            // logger.error(e.getMessage(), e);
        }
        finally {
            close(out);
            close(os);
        }
    }

    /**
     * 关闭流
     * 
     * @param c
     */
    public static void close(Closeable c) {
        if (null != c) {
            try {
                c.close();
                c = null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
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
            close(fis);
            close(fos);
        }
    }

    public static void main(String[] args) {
        // args = new String[4];
        // args[0] = "F:\\FlowAnalysisProject\\src";
        // args[1] = "F:\\tomstools\\code\\eclipse-plugin\\tomstools\\src2";
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
        } else {
            System.err.println("Invalid arguments!");
            System.out.println("Usage:");
            System.out.println("    srcFileName destFileName srcCharsetName destCharsetName [0|1]");
        }
        // new File(srcFile.getAbsoluteFile() + ".del").delete();
    }

    /**
     * 转换文件字符集 支持文件/目录的自转换（转换后替换源文件）
     * 
     * @param srcFileName
     *            待转换文件名/目录
     * @param destFileName
     *            转换后文件名/目录
     * @param srcCharsetName
     *            待转换文件字符集
     * @param destCharsetName
     *            转换后文件字符集
     * @param keepDirectStruct
     *            如果是指定目录进行转换，转换后是否保持原目录结构
     */
    public static void convertFileCharset(String srcFileName, String destFileName,
            String srcCharsetName, String destCharsetName, boolean keepDirectStruct) {
        System.out.println("source charset:" + srcCharsetName + " dest charset:" + destCharsetName);
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        
        // 同目录下进行替换
        if (srcFile.getAbsolutePath().equals(destFile.getAbsolutePath())
                || (srcFile.isFile() && srcFile.getParentFile().getAbsolutePath()
                        .equals(destFile.getAbsolutePath()))) {
            transeSelfCharset(srcFile, srcCharsetName, destCharsetName);
        } else {
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
        } else if (srcFile.isDirectory()) {
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
            } else {
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
        } else {
            saveFile(destFile, fileContent, destCharsetName);
        }
    }
}
