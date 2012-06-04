package org.tomstools.common.merge.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomstools.common.util.FileUtil;

import junit.framework.TestCase;

public class FileMergeControlerTest extends TestCase {

    public void testReplaceJs() {
        // fail("Not yet implemented");
    }

    public void testReplaceCss1() {
        String content = "abc123 url(a.gif) url('b.gif')=url(\"c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(b/a.gif) url('b/b.gif')=url(\"b/c.gif\")", result);
    }

    public void testReplaceCss2() {
        String content = "abc123 url(./a.gif) url('./b.gif')=url(\"./c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(b/a.gif) url('b/b.gif')=url(\"b/c.gif\")", result);
    }

    public void testReplaceCss3() {
        String content = "abc123 url(./i/a.gif) url('./i/b.gif')=url(\"./i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(b/i/a.gif) url('b/i/b.gif')=url(\"b/i/c.gif\")", result);
    }

    public void testReplaceCssWithParentPath() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:/a/b";
        String pagePath = "c:/a/";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(i/a.gif) url('i/b.gif')=url(\"i/c.gif\")", result);
    }

    public void testReplaceCssWithBrotherPath() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a\\c";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")", result);
    }

    public void testReplaceCssWithOtherPath1() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a\\c\\d";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(../../i/a.gif) url('../../i/b.gif')=url(\"../../i/c.gif\")",
                result);
    }

    public void testReplaceCssWithOtherPath2() {
        String content = "abc123 url(../i/a.gif) url('../i/b.gif')=url(\"../i/c.gif\")";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\c\\d";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123 url(../../a/i/a.gif) url('../../a/i/b.gif')=url(\"../../a/i/c.gif\")",
                result);
    }

    public void testReplaceCssWithNoMatches() {
        String content = "abc123url(a.gif)";
        String contentPath = "c:\\a\\b";
        String pagePath = "c:\\a";
        String result = new FileMergeControler().replaceCss(content, contentPath,
                new File(pagePath));
        assertEquals("abc123url(a.gif)", result);
    }

    public void testMergeCssWithTwoFile() throws Exception {
        String fileId = "222";
        String fileType = "css";
        FileMergeControler controler = new FileMergeControler();

        String outputPath = "c:/";
        String fileName = "c:/p/a.css";
        String fileContent = "body{background-image:url(i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        fileName = "c:/p/a/b/c.css";
        fileContent = "body{background-image:url(../../i/a.gif)}";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);

        File outFile = new File(outputPath + File.separator
                + controler.getMergedFileName(fileId, fileType));
        // outFile.delete();
        controler.merge("UTF-8");

        assertTrue("The file is not exists!" + outFile.getAbsolutePath(), outFile.exists());
        // 内容校验
        assertEquals(
                "body{background-image:url(p/i/a.gif)}\nbody{background-image:url(p/i/a.gif)}\n",
                FileUtil.getFileContent(outFile));

    }

    public void testMergeCssWithThreeFile() throws Exception {
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

        File outFile = new File(outputPath + File.separator
                + controler.getMergedFileName(fileId, fileType));
        // outFile.delete();
        controler.merge("UTF-8");

        assertTrue("The file is not exists!" + outFile.getAbsolutePath(), outFile.exists());
        // 内容校验
        assertEquals(
                "body{background-image:url(p/i/a.gif)}\nbody{background-image:url(p/i/a.gif)}\nbody{background-image:url(p/a/i/a.gif)}\n",
                FileUtil.getFileContent(outFile));
    }

    public void testMergeJsWithTwoFile() throws Exception {
        String fileId = "2";
        String fileType = "js";
        FileMergeControler controler = new FileMergeControler();

        String outputPath = "e:/";
        String fileName = "e:/p/a.js";
        String fileContent = "if(document.images)\nnew Image().src='i/a.gif';\n";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);
        fileName = "e:/p/a/b/c.js";
        fileContent = "var image = new Image();\nimage.src = \"../../i/a.gif\";\nimage .src='a/gif';\n";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);

        File outFile = new File(outputPath + File.separator
                + controler.getMergedFileName(fileId, fileType));
        // outFile.delete();
        controler.merge("UTF-8");

        assertTrue("The file is not exists!" + outFile.getAbsolutePath(), outFile.exists());
        // 内容校验
        assertEquals(
                "if(document.images)\nnew Image().src='p/i/a.gif';\nvar image = new Image();\nimage.src = \"p/i/a.gif\";\nimage .src='a/gif';\n",
                FileUtil.getFileContent(outFile));
    }

    public void testMergeJsWithThreeFile() throws Exception {
        String fileId = "333";
        String fileType = "js";
        FileMergeControler controler = new FileMergeControler();

        String outputPath = "f:/";
        String fileName = "f:/p/a.js";
        String fileContent = "function(){\nvar i = new Image();\nalert(i);\ni.src = 'i/a.gif';\n";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);

        fileName = "f:/p/a/b/c.js";
        fileContent = "var img = new Image();\nimg.src=\"../../i/a.gif\";\nalert(img.src);\nimg.src='../i/a.gif';\n";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);

        fileName = "f:/p/a/b.js";
        fileContent = "img = new Image();\nimg.src='i/a.gif';\n";
        prepareFile(fileName, fileContent);
        controler.addFile(fileId, fileName, fileType, outputPath);

        File outFile = new File(outputPath + File.separator
                + controler.getMergedFileName(fileId, fileType));
        // outFile.delete();
        controler.merge("UTF-8");

        assertTrue("The file is not exists!" + outFile.getAbsolutePath(), outFile.exists());
        // 内容校验
        assertEquals(
                "function(){\nvar i = new Image();\nalert(i);\ni.src = 'p/i/a.gif';\nvar img = new Image();\nimg.src=\"p/i/a.gif\";\nalert(img.src);\nimg.src='p/a/i/a.gif';\nimg = new Image();\nimg.src='p/a/i/a.gif';\n",
                FileUtil.getFileContent(outFile));
    }

    public static void main(String[] args) throws Exception {
        // String content =
        // "var image = new Image();\nimage.src = \"../../i/a.gif\";\nImage.src='a/gif';image.src='a/gif';";
        // String regex =
        // "[\\W](\\w+)\\s*=\\s*(new ){0,1}Image\\(\\)\\s*;(.*?(\\1)\\.src\\s*=\\s*['\"](.*?)['\"];)+?";
        String content = "aaa=123;aab=1;aac=b;";
        String regex = "aaa=.*?(aa\\w*)+";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            System.out.println(matcher.groupCount());
            for (int i = 0; i < matcher.groupCount(); i++) {
                System.out.println(matcher.group(i + 1));
            }

            System.out.println("----------------------------------------");
        }
    }

    private void prepareFile(String fileName, String fileContent) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            if (!file.delete()) {
                fail("delete file failed!" + file.getAbsolutePath());
            }
        }

        file.getParentFile().mkdirs();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(fileContent);
        writer.close();
    }
}
