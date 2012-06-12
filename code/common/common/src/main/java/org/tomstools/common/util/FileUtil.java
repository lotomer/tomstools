/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.util;

import java.io.File;
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

import org.tomstools.common.log.Logger;

/**
 * 文件操作工具类
 * 
 * @author lotomer
 * @date 2011-12-20
 * @time 下午03:26:24
 */
public final class FileUtil {
    private static final Logger logger = Logger.getLogger(FileUtil.class);
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
        logger.info("get file content. fileName:" + file.getAbsolutePath() + " charset:"
                + charsetName);

        if (file.isFile()) {
            try {
                RandomAccessFile inf = new RandomAccessFile(file, "r");
                long inputLength = file.length();
                FileChannel inc = inf.getChannel();
                MappedByteBuffer inputData = inc.map(FileChannel.MapMode.READ_ONLY, 0, inputLength);
                Charset charset = Charset.forName(charsetName);
                CharsetDecoder decoder = charset.newDecoder();
                CharBuffer cb = decoder.decode(inputData);
                inf.close();
                return cb.toString();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("The file is not exists or is not a file!" + file.getAbsolutePath());
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
            logger.error(e.getMessage());
            return file.getAbsolutePath();
        }
    }

    public static void saveFile(String fileName, String content, String charsetName) {
        if (Utils.isEmpty(fileName) || Utils.isEmpty(content)) {
            return;
        }
        logger.info("save file:" + fileName + " charset:" + charsetName);
        File file = new File(fileName);
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
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
