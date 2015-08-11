/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.common;


/**
 * 密码安全工具
 * 
 * @author admin
 * @date 2015年6月26日
 * @time 下午8:31:21
 * @version 1.0
 */
public interface Encryptable {

    /** 对字符串进行加密 */
    public byte[] encrypt(byte[] src) throws Exception;
    /** 对字符串进行解密 */
    public byte[] decrypt(byte[] buff) throws Exception;
}
