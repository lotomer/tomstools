package org.lotomer.plugins.tomstools.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
    // ------------------------------国际化-------------------------begin
    // 国际化命令
    public static final String P_NATIVE_COMMAND = "nativeCommand";
    // 国际化资源文件的后缀部分
    public static final String P_NATIVE_FILE_POSTFIX = "nativeFilePostfix";
    // 样例：国际化之前
    public static final String P_NATIVE_FILE_SAMPLE = "nativeFileSample";
    // 样例：国际化之后
    public static final String P_NATIVE_FILE_SAMPLE_FILLED = "nativeFileSampleFilled";

    // 默认值
    // 国际化命令
    public static final String P_NATIVE_COMMAND_DEFAULT = "cmd /c native2ascii \"%1\" \"%2\"";

    // 国际化文件后缀
    public static final String P_NATIVE_FILE_POSTFIX_DEFAULT = "zh_CN";

    // 样例文件名
    public static final String P_NATIVE_FILE_SAMPLE_DEFAULT = "sample.properties";

    // 国际化样例文件名
    public static final String P_NATIVE_FILE_SAMPLE_FILLED_DEFAULT = "sample_zh_CN.properties";
    // ------------------------------国际化-------------------------end

    // ------------------------------复制路径-------------------------begin
    // 路径前缀
    public static final String P_COPY_PATH_PREFIX = "copyPathPrefix";
    // ------------------------------复制路径-------------------------end

    // ------------------------------命令行-------------------------begin
    // 启动命令行的命令
    public static final String P_COMMAND_LINE_COMMAND = "commandLineCommand";
    // 默认值
    public static final String P_COMMAND_LINE_COMMAND_DEFAULT = "cmd.exe /k start \"command\" /D \"%1\"";
    // ------------------------------命令行-------------------------end

    // ------------------------------资源管理器-------------------------begin
    // 在资源管理器中打开文件夹的命令
    public static final String P_OPEN_IN_EXPLORER_COMMAND_FOR_DIR = "openInExplorerCommand4dir";
    // 在资源管理器中定位文件的命令
    public static final String P_OPEN_IN_EXPLORER_COMMAND_FOR_FILE = "openInExplorerCommand4file";
    // 默认打开文件夹的命令
    public static final String P_OPEN_IN_EXPLORER_COMMAND_FOR_DIR_DEFAULT = "Explorer.exe  \"%1\"";
    // 默认定位文件的命令
    public static final String P_OPEN_IN_EXPLORER_COMMAND_FOR_FILE_DEFAULT = "Explorer.exe /select, \"%1\"";
    // ------------------------------资源管理器-------------------------end
    // ------------------------------文件字符集转换-------------------------begin
    public static final String P_CONVERT_FILE_CHARSET_SRC_CHARSET = "srcFileCharset";
    public static final String P_CONVERT_FILE_CHARSET_DEST_CHARSET = "destFileCharset";
    public static final String P_CONVERT_FILE_CHARSET_SRC_CHARSET_DEFAULT = "GBK";
    public static final String P_CONVERT_FILE_CHARSET_DEST_CHARSET_DEFAULT = "UTF-8";
    public static final String P_CONVERT_FILE_CHARSET_SRC_PATH = "srcPath";
    public static final String P_CONVERT_FILE_CHARSET_DEST_PATH = "destPath";
    public static final String P_CONVERT_FILE_CHARSET_SRC_PATH_DEFAULT = "";
    public static final String P_CONVERT_FILE_CHARSET_DEST_PATH_DEFAULT = "";
    // ------------------------------文件字符集转换-------------------------end
}
