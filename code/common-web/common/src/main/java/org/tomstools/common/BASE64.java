/**
 * copyright (a) 2010-2015 tomstools.org. All rights reserved.
 */
package org.tomstools.common;

import org.apache.commons.codec.binary.Base64;

/**
 * BASE64加密
 * @author admin
 * @date 2015年6月26日 
 * @time 下午9:08:36
 * @version 1.0
 */
public class BASE64 implements Encryptable {
    private Encryptable enc;
    public BASE64(){
        
    }
    public BASE64(Encryptable enc){
        this.enc=enc;
    }
    /*
     * @since 1.0
     */
    public byte[] encrypt(byte[] src) throws Exception {
        if (null != this.enc){
            return Base64.encodeBase64(this.enc.encrypt(src));
        }else{
            return Base64.encodeBase64(src);
        }
    }

    /*
     * @since 1.0
     */
    public byte[] decrypt(byte[] buff) throws Exception {
        if (null != this.enc){
            return this.enc.decrypt(Base64.decodeBase64(buff));
        }else{
            return Base64.decodeBase64(buff);
        }
    }

}
