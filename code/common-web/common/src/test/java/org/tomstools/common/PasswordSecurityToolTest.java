package org.tomstools.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tomstools.common.BASE64;
import org.tomstools.common.Encryptable;

public class PasswordSecurityToolTest {

    public PasswordSecurityToolTest() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEncrypt() throws Exception {
        String msg = "123";
        long st = System.currentTimeMillis();
        Encryptable de1 = new BASE64();// DES();
        System.out.println(System.currentTimeMillis() - st);
        st = System.currentTimeMillis();
        int count = 100000;
        byte[] encontent = null;
        byte[] decontent = null;
        for (int i = 0; i < count; i++) {
            encontent = de1.encrypt(msg.getBytes());
            decontent = de1.decrypt(encontent);
        }
        System.out.println("明文是:" + msg);
        System.out.println("加密后:" + new String(encontent));
        System.out.println("解密后:" + new String(decontent));

        System.out.println(System.currentTimeMillis() - st);
    }
}
