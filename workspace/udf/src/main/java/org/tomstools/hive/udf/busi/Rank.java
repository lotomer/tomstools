/**
 * copyright (a) 2010-2014 tomstools.org. All rights reserved.
 */
package org.tomstools.hive.udf.busi;

/**
 * 
 * @author admin
 * @date 2014年8月4日 
 * @time 下午10:47:35
 * @version 1.0
 */
public class Rank {
    private static int MAX_VALUE = 50;
    private static String comparedColumn[] = new String[MAX_VALUE];
    private static int rowNum = 1;

    public static int execute(Object... args) {
        String columnValue[] = new String[args.length];
        for (int i = 0; i < args.length; i++){
            columnValue[i] = args[i].toString();
        }
        if (rowNum == 1) {
            for (int i = 0; i < columnValue.length; i++)
                comparedColumn[i] = columnValue[i];
        }

        for (int i = 0; i < columnValue.length; i++) {
            if (!comparedColumn[i].equals(columnValue[i])) {
                for (int j = 0; j < columnValue.length; j++) {
                    comparedColumn[j] = columnValue[j];
                }
                rowNum = 1;
                return rowNum++;
            }
        }
        return rowNum++;
    }
}
