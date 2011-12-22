package org.tomstools.common.util;

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
}
