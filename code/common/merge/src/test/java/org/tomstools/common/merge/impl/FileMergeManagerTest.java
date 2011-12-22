package org.tomstools.common.merge.impl;

import org.tomstools.common.merge.file.FileMergeControler;

import junit.framework.TestCase;

public class FileMergeManagerTest extends TestCase {

    public void testMerge() {
        fail("Not yet implemented");
    }

    public void testGetMergedInfo() {
        // 1 数据来源：数据库、文件。默认提供文件方式，数据库方式需自己实现
        // 2 应用场景：添加文件信息（公用）、获取文件合并信息（WEB）、合并文件（打包）
        FileMergeControler mergeManager = new FileMergeControler();
        // 添加
        //mergeManager.addFile(fileId, fileName, fileType);
        // 获取文件合并信息
        //mergeManager.getMergedInfo(fileId, fileType);
        // 执行合并
        mergeManager.merge("");
    }

}
