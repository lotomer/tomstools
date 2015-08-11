/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.common;

import java.security.MessageDigest;

/**
 * 
 * @author admin
 * @date 2015年7月9日 
 * @time 下午4:53:13
 * @version 1.0
 */
public class MD5 implements Encryptable {
    private static final char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private Encryptable other;
    public MD5(Encryptable other) {
        this.other = other;
    }
    public MD5() {
    }
    public static void main(String[] args) throws Exception {
        MD5 md5 = new MD5();
        System.out.println(new String(md5.encrypt("20121221".getBytes())));
        System.out.println(new String(md5.encrypt("加密".getBytes())));
    }
    public byte[] encrypt(byte[] src) throws Exception {
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        // 使用指定的字节更新摘要
        if (null != this.other){
            mdInst.update(other.encrypt(src));
        }else{
            mdInst.update(src);
        }
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str).getBytes();
    }
    public byte[] decrypt(byte[] buff) throws Exception {
        throw new Exception("Not Implemented");
    }
}
