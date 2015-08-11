/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.common;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 密码安全工具
 * 
 * @author admin
 * @date 2015年6月26日
 * @time 下午8:31:21
 * @version 1.0
 */
public class DES implements Encryptable {
    private static final Log LOG = LogFactory.getLog(DES.class);
    // KeyGenerator 提供对称密钥生成器的功能，支持各种算法
    private KeyGenerator keygen;
    // SecretKey 负责保存对称密钥
    private SecretKey deskey;
    // Cipher负责完成加密或解密工作
    private Cipher en;
    private Cipher de;
    private boolean isReady;
    public DES() {
        try {
            Security.addProvider(new com.sun.crypto.provider.SunJCE());
            // 实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)
            keygen = KeyGenerator.getInstance("DES");
            // 生成密钥
            deskey = keygen.generateKey();
            // 生成Cipher对象,指定其支持的DES算法
            en = Cipher.getInstance("DES");
            de = Cipher.getInstance("DES");
            // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
            en.init(Cipher.ENCRYPT_MODE, deskey);
            // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
            de.init(Cipher.DECRYPT_MODE, deskey);
            isReady = true;
        } catch (Exception e) {
            isReady = false;
            LOG.error(e.getMessage(), e);
        }
    }

    public byte[] encrypt(byte[] src) throws Exception {
        if (!isReady){
            throw new Exception("The DES is not ready!");
        }
        return en.doFinal(src);
    }

    public byte[] decrypt(byte[] buff) throws Exception {
        if (!isReady){
            throw new Exception("The DES is not ready!");
        }
        return de.doFinal(buff);
    }
}
