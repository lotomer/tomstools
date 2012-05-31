package org.tomstools.common.util;

import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class FileUtilTest extends TestCase {
    public void testGetFileExtWithShortFileName() throws Exception {
        String fileName = "a.txt";
        assertEquals("txt", FileUtil.getFileExt(fileName));
    }
    
    public void testGetFileExtWithFullFileName() throws Exception {
        String fileName = "z:/a/b/c/a.txt";
        assertEquals("txt", FileUtil.getFileExt(fileName));
    }
    
    public void testGetFileExtWithEmptyFileName() {
        assertEquals("", FileUtil.getFileExt(""));
        assertEquals("", FileUtil.getFileExt(""));
        assertEquals("", FileUtil.getFileExt("   "));
    }
    
    public void testGetFileExtWithoutExt() throws Exception {
        String fileName = "z:/a/b/c/a.";
        assertEquals("", FileUtil.getFileExt(fileName));
    }
    
    public void testGetFileExtWithDirectory() throws Exception {
        String fileName = "z:/a/b/c/d";
        assertEquals("", FileUtil.getFileExt(fileName));
    }
    private void prepareFile(String fileName) throws IOException{
        FileOutputStream fo = new FileOutputStream(fileName);
        for (int i = 0; i < 10000; ++i){
            fo.write(String.valueOf(i).getBytes());
        }        
        fo.close();
    }
    public void testReadFileByInputStream() throws Exception {
        String fileName = "d:/a.txt";
        prepareFile(fileName);
    }
}
