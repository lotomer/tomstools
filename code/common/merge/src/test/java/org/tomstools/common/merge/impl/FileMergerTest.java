package org.tomstools.common.merge.impl;

import org.tomstools.common.merge.file.FileMerger;

import junit.framework.TestCase;


public class FileMergerTest extends TestCase{
    public void testMerge() {
        FileMerger merger = new FileMerger();
        merger.add("Z:/1.txt");
        merger.add("z:/2.log");
        merger.add("z:/3.txt");
        merger.merge("z:/4.txt", null);
    }

}
