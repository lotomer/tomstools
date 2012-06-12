package org.tomstools.common.util;

import java.io.File;
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
    public void testGenerateAcstractPath1() throws Exception {
        String file1 = "c:/a/b/c";
        String file2 = "c:/a/b";
        assertEquals("../", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath2() throws Exception {
        String file1 = "c:/a/b/c";
        String file2 = "c:/a/b/d";
        assertEquals("../d", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath3() throws Exception {
        String file1 = "c:/a";
        String file2 = "c:/a/b";
        assertEquals("b", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath4() throws Exception {
        String file1 = "c:/a";
        String file2 = "c:/a/b";
        assertEquals("b", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath5() throws Exception {
        String file1 = "c:/a";
        String file2 = "c:/a/b.txt";
        assertEquals("b.txt", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath6() throws Exception {
        String file1 = "c:/a/c";
        String file2 = "c:/a/b.txt";
        assertEquals("../b.txt", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath7() throws Exception {
        String file1 = "c:/a/c";
        String file2 = "c:/a/b/d.txt";
        assertEquals("../b/d.txt", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath8() throws Exception {
        String file1 = "c:/a/c/e.txt";
        String file2 = "c:/a/b/d.txt";
        assertEquals("../../b/d.txt", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath9() throws Exception {
        String file1 = "c:/a/";
        String file2 = "c:/a/b";
        assertEquals("b", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath10() throws Exception {
        String file1 = "c:/a/";
        String file2 = "c:/a/b/";
        assertEquals("b", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
    public void testGenerateAcstractPath11() throws Exception {
        String file1 = "c:/a";
        String file2 = "c:/a/b/";
        assertEquals("b", FileUtil.generateAbstractPath(new File(file1),new File(file2)));
    }
}
