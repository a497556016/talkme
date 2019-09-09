package com.heshaowei.myproj.im.server.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class VideoFrameUtil {
    public static String JAR_PATH;
    public static String JAR_NAME;

    public static void getFirstFrame(String sourcePath, String targetPath) throws Exception{
        getFirstFrame(null, sourcePath, targetPath);
    }

    public static void getFirstFrame(String jarPath, String sourcePath, String targetPath) throws Exception {
        if(null == jarPath) {
            //默认jar路径为当前项目所在路径
            jarPath = JAR_PATH;
        }
        if(null == jarPath || null == JAR_NAME) {
            throw new Exception("未知的jar路径");
        }
        String[] command = new String[]{"java", "-jar", jarPath + File.separatorChar + JAR_NAME, sourcePath, targetPath};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStream in = p.getInputStream();
        p.waitFor();
        if(!(p.exitValue() != 0)){
            //执行异常
        }
        IOUtils.readLines(in, CharEncoding.UTF_8).forEach(s -> System.out.println(s));
    }

    private static String getPath(){
        try {
            return ResourceUtils.getURL("classpath:").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getPath());
    }
}
