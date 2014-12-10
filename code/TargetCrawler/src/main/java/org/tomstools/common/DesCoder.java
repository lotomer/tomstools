/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.common;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 加解密工具
 * 
 * @author admin
 * @date 2014年12月10日
 * @time 上午10:20:04
 * @version 1.0
 */
public class DesCoder {
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "DES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

    private static DesCoder self;

    // private final String DEFAULT_CIPHER_ALGORITHM =
    // "DES/ECB/ISO10126Padding";
    public static DesCoder getInstance() throws Exception {
        if (null == self) {
            self = new DesCoder();
        }

        return self;
    }

    private DesCoder() {
    }

    /**
     * 初始化密钥
     * 
     * @return byte[] 密钥
     * @throws Exception
     */
    public byte[] initSecretKey() throws Exception {
        // 返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        // 初始化此密钥生成器，使其具有确定的密钥大小
        kg.init(56);
        // 生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 转换密钥
     * 
     * @param key 二进制密钥
     * @return Key 密钥
     * @throws Exception
     */
    private Key toKey(byte[] key) throws Exception {
        // 实例化DES密钥规则
        DESKeySpec dks = new DESKeySpec(key);
        // 实例化密钥工厂
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        // 生成密钥
        SecretKey secretKey = skf.generateSecret(dks);
        return secretKey;
    }

    /**
     * 加密
     * 
     * @param data 待加密数据
     * @param key 密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     * 
     * @param data 待加密数据
     * @param key 二进制密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     * 
     * @param data 待加密数据
     * @param key 二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[] 加密数据
     * @throws Exception
     */
    public byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }

    /**
     * 加密
     * 
     * @param data 待加密数据
     * @param key 密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[] 加密数据
     * @throws Exception
     */
    public byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        // 实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        // 使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * 
     * @param data 待解密数据
     * @param key 二进制密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     * 
     * @param data 待解密数据
     * @param key 密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     * 
     * @param data 待解密数据
     * @param key 二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[] 解密数据
     * @throws Exception
     */
    public byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        // 还原密钥
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    /**
     * 解密
     * 
     * @param data 待解密数据
     * @param key 密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[] 解密数据
     * @throws Exception
     */
    public byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        // 实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        // 使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 执行操作
        return cipher.doFinal(data);
    }

    public byte[] decrypt(String data, byte[] key) throws Exception {
        return decrypt(Hex.decodeHex(data.toCharArray()), key);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Invalid arguments!");
            System.out.println("Usage: key text");
            System.exit(-1);
        }
        DesCoder des = DesCoder.getInstance();
        byte[] key = args[0].getBytes();

        String data = args[1];
        byte[] encryptData = des.encrypt(data.getBytes(), key);
        String s = Hex.encodeHexString(encryptData);
        System.out.println(s);
        // byte[] decryptData = des.decrypt(s, key);

    }
}
