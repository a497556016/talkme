package com.heshaowei.myproj.im.server.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public class FileConvertUtil {
    public static String toBase64(String path, String contentType){
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            Base64.Encoder encoder = Base64.getEncoder();
            try {
                String base64 = encoder.encodeToString(FileUtils.readFileToByteArray(file));
                return "data:" + contentType + ";base64," + base64;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void fromBase64(String base64, String savePath){
        Base64.Decoder decoder = Base64.getDecoder();
        if(base64.contains(",")){
            base64 = base64.split(",")[1];
        }
        OutputStream outputStream = null;
        try {
            byte[] buffer = decoder.decode(base64);
            java.io.File dest = new java.io.File(savePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            outputStream = new FileOutputStream(dest);
            outputStream.write(buffer);
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
