package org.tomstools.common.merge.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.tomstools.common.util.FileUtil;

import junit.framework.TestCase;

public class FileMergeControlerTest extends TestCase {

    public void testReplaceJs() {
        //fail("Not yet implemented");
    }
    public void testReplaceCss1() {
        String content = "abc123 url(a.gif) url('b.gif')=url(\"c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(b/a.gif) url('b/b.gif')=url(\"b/c.gif\")", result);
    }
    public void testReplaceCss2() {
        String content = "abc123 url(./a.gif) url('./b.gif')=url(\"./c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(b/a.gif) url('b/b.gif')=url(\"b/c.gif\")", result);
    }
    public void testReplaceCss3() {
        String content = "abc123 url(./i/a.gif) url('./i/b.gif')=url(\"./i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(b/i/a.gif) url('b/i/b.gif')=url(\"b/i/c.gif\")", result);
    }    
    public void testReplaceCssWithParentPath() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:/a/b";
        String pagePath = "c:/a/";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(i/a.gif) url('i/b.gif')=url(\"i/c.gif\")", result);
    }
    public void testReplaceCssWithBrotherPath() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a\\c";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")", result);
    }
    public void testReplaceCssWithOtherPath1() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a\\c\\d";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(../../i/a.gif) url('../../i/b.gif')=url(\"../../i/c.gif\")", result);
    }
    public void testReplaceCssWithOtherPath2() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\c\\d";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123 url(../../a/i/a.gif) url('../../a/i/b.gif')=url(\"../../a/i/c.gif\")", result);
    }
    
    public void testReplaceCssWithNoMatches() {
        String content = "abc123url(a.gif)";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,new File(pagePath));
        assertEquals("abc123url(a.gif)", result);
    }

    public void testMergeWithTwoFile() throws Exception {
        String fileId = "2";
        String fileType = "css";
        FileMergeControler controler = new FileMergeControler();
        
        String outputPath = "d:/";
        String fileName = "d:/p/a.css";
        String fileContent = "body{background-image:url(i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        fileName = "d:/p/a/b/c.css";
        fileContent = "body{background-image:url(../../i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        
        File outFile = new File(outputPath + File.separator + controler.getMergedFileName(fileId, fileType));
        outFile.delete();        
        controler.merge("UTF-8");
        
        assertTrue(outFile.exists());
        //内容校验
        assertEquals("body{background-image:url(p/i/a.gif)}body{background-image:url(p/i/a.gif)}", FileUtil.getFileContent(outFile));
        
    }
    public void testMergeWithThreeFile() throws Exception {
        String fileId = "3";
        String fileType = "css";
        FileMergeControler controler = new FileMergeControler();
        
        String outputPath = "d:/";
        String fileName = "d:/p/a.css";
        String fileContent = "body{background-image:url(i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        
        fileName = "d:/p/a/b/c.css";
        fileContent = "body{background-image:url(../../i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        
        fileName = "d:/p/a/b.css";
        fileContent = "body{background-image:url(i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        
        File outFile = new File(outputPath + File.separator + controler.getMergedFileName(fileId, fileType));
        outFile.delete();
        controler.merge("UTF-8");

        assertTrue(outFile.exists());
        //内容校验
        assertEquals("body{background-image:url(p/i/a.gif)}body{background-image:url(p/i/a.gif)}body{background-image:url(p/a/i/a.gif)}", FileUtil.getFileContent(outFile));
    }
    
    private void prepareFile(String fileName, String fileContent) throws IOException {
        File file = new File(fileName);
        if (file.exists()){
            if (!file.delete()){
                fail("delete file failed!" + file.getAbsolutePath());
            }
        }else{
            file.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContent);
            writer.close();
        }
    }
}
