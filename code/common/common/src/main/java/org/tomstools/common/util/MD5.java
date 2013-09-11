package org.tomstools.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private MessageDigest md;

    public MD5() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");
    }

    /**
     * 将输入字符串经过MD5处理后返回
     * @param inputString 待处理字符串
     * @return MD5之后的结果
     */
    public String md5(String inputString) {
        md.update(inputString.getBytes());
        byte b[] = md.digest();

        int i;

        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    public static void main(String args[]) throws NoSuchAlgorithmException {
        MD5 md5 = new MD5();
        long t = System.currentTimeMillis();
        for (int i = 0; i < 100000; ++i) {
            md5.md5("kjaweiaskdfjlasjdfoi之阿斯蒂芬");
        }
        System.out.println(md5.md5("www.google.com") + ":" + (System.currentTimeMillis() - t));
    }
}
