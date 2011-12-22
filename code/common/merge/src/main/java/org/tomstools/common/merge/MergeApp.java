/**
 * copyright (a) 2010-2011 tomstools.org. All rights reserved.
 */
package org.tomstools.common.merge;

import org.tomstools.common.merge.manage.WebFileManager;
import org.tomstools.common.merge.manage.WebFileManagerFactory;

/**
 * 合并应用程序
 * @author lotomer
 * @date 2011-12-21
 * @time 上午11:44:00
 */
public final class MergeApp {
    private static final String COMMAND_DEFINE_PRE = "-D";
    private static final String COMMAND_COMPRESS = "-compress";

    public static void main(String[] args) {
        WebFileManager webFileManager = WebFileManagerFactory.getInstance().getWebFileManager();
        String charset = "UTF-8"; // 默认文件编码
        for (int i = 0; i < args.length; ++i) {
        String arg = args[i];
        if ("-debug".equalsIgnoreCase(arg)) {
            String debug = null;
            if (++i < args.length) {
                debug = args[i];
            } else {
                debug = "true";
            }
            if ("true".equalsIgnoreCase(debug)){
                webFileManager.setDebug(true);
            }else{
                webFileManager.setDebug(false);
            }
        } else if ("-charset".equalsIgnoreCase(arg)) {
            if (++i < args.length) {
                charset = args[i];
            }
        } else if (arg.startsWith(COMMAND_COMPRESS)) {
            webFileManager.setNeedCompress(true);
            if (arg.startsWith(COMMAND_COMPRESS + ":")){
                String cmd = arg.substring(COMMAND_COMPRESS.length() + 1);
                if ("deleteSourceFile".equalsIgnoreCase(cmd)){
                    webFileManager.setNeedDeleteSourceFileForCompress(true);
                }
            }
        } else if (arg.startsWith(COMMAND_DEFINE_PRE)) {
            String[] param = arg.substring(COMMAND_DEFINE_PRE.length()).split("=", 2);
            if (param.length == 2) {
                webFileManager.addVariable(param[0], param[1]);
            }
        }else if ("-h".equalsIgnoreCase(arg) || "-help".equalsIgnoreCase(arg)) {
            printUsage();
            System.exit(0);
        } 
        }
        // 初始化数据
        webFileManager.init();
        //执行合并
        webFileManager.merge(charset);
    }

    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("参数说明：");
        System.out.println("    -h | -help");
        System.out.println("        打印参数说明");
        System.out.println("    -debug true|false");
        System.out.println("        是否开启调试模式。true 调试模式；false 非调试模式。");
        System.out.println("        不设置该参数则默认开启调试模式");
        System.out.println("    -charset charset_name");
        System.out.println("        指定文件编码。不指定则默认UTF-8");
        System.out.println("    -compress[:deleteSourceFile]");
        System.out.println("        开启文件压缩功能。不设置该参数则默认不压缩。如果指定了:deleteSourceFile，则删除压缩后源文件，默认不删除");
        System.out.println("    -D");
        System.out.println("        设置变量。例如，设置变量A和B的值分别是1和2的方法是：-DA=1 -DB=2");
        System.out.println("详细介绍：");
        System.out.println("  【变量设置】");
        System.out.println("        如果需要指定文件合并后的输出路径，则需要设置变量OUTPUT_加上文件类型如JS/CSS，如-DOUTPUT_JS=Z:/webapp/js");
        System.out.println("        如果没有指定文件合并后的输出路径，则默认输出到当前目录");
        System.out.println("        如果需要指定WEB前台文件的配置文件，通过设置变量config来指定。默认配置文件名为：webFileConfig.properties");
        System.out.println("  【配置文件】");
        System.out.println("        WEB前台文件的配置文件格式：一行一条、使用 key=value的方式配置");
        System.out.println("            key  组成格式：web. + 文件类型(js/css) + . + 文件标识(同一文件类型内必须唯一)");
        System.out.println("            value组成格式：文件完整路径（可以包含变量，变量格式是：${变量名}。多个文件路径之间以英文逗号\",\"分隔）");
        
    }
}
