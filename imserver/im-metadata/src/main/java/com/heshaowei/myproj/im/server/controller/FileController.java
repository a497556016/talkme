package com.heshaowei.myproj.im.server.controller;

import com.google.common.collect.Maps;
import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.server.dto.FileReq;
import com.heshaowei.myproj.im.server.enums.MediaTypes;
import com.heshaowei.myproj.im.server.properties.FileProperty;
import com.heshaowei.myproj.im.server.repository.FileRepository;
import com.heshaowei.myproj.im.server.utils.FileConvertUtil;
import com.heshaowei.myproj.im.server.utils.FrameGrabberKit;
import com.heshaowei.myproj.utils.image.GifUtils;
import com.heshaowei.myproj.utils.image.ImageHandler;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileProperty fileProperty;

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public Result upload(HttpServletRequest request, @RequestBody FileReq fileReq){
//        File f = new File();

        String fileName = UUID.randomUUID().toString();
        String thumbnailName = fileName+"_thumb";
        String fileNameSuffix = "";
        String contentType = null;

        if(fileReq.getFile().startsWith("data:")&&fileReq.getFile().contains(";base64,")){
            contentType = fileReq.getFile().split(";base64,")[0].substring(5);
        }

        if(null != contentType){
//            f.setContentType(contentType);
            String mediaType = contentType.split("/")[0];
            String suffix = contentType.split("/")[1];

            switch (mediaType){
                default:break;
                case "audio":
                    if(suffix.equals("mpeg")){
                        fileNameSuffix = ".m4a";
                    }
                    break;
                case "image":
                    if(suffix.equals("jpeg")){
                        fileNameSuffix = ".jpg";
                    }else if(suffix.equals("png")){
                        fileNameSuffix = ".png";
                    }else if(suffix.equals("gif")){
                        fileNameSuffix = ".gif";
                    }
                    break;
                case "video":
                    if(suffix.equals("mp4")){
                        fileNameSuffix = ".mp4";
                    }
                    break;
            }
        }else {
            if (MediaTypes.AUDIO.equals(fileReq.getMediaType())) {
                fileNameSuffix = ".m4a";
                contentType = "audio/mpeg";
            } else if (MediaTypes.PICTURE.equals(fileReq.getMediaType())) {
                fileNameSuffix = ".jpg";
                contentType = "image/jpeg";
            }
        }
//        f.setContentType(contentType);
        String path = "\\im\\"+fileReq.getMediaType().name()+"\\"+fileName+fileNameSuffix;
//        f.setPath(path);
//        f.setFileName(fileName);


        String src = fileProperty.getFileSavePath() + path;
        FileConvertUtil.fromBase64(fileReq.getFile(), src);
//        this.fileRepository.save(f);

        Map<String, Object> result = Maps.newHashMap();
        result.put("path", path);
        //生成缩略图返回
        String thumbnail = "\\im\\"+fileReq.getMediaType().name()+"\\"+thumbnailName+fileNameSuffix;
        if(MediaTypes.PICTURE.equals(fileReq.getMediaType())) {
            if("image/gif".equals(contentType)){
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(this.fileProperty.getFileSavePath() + thumbnail));
                    GifUtils.zoomW(src, outputStream, 180);
                    result.put("thumbnail", thumbnail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    new ImageHandler(src, this.fileProperty.getFileSavePath() + thumbnail).scaleW(100).writeToFile();
                    result.put("thumbnail", thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(MediaTypes.VIDEO.equals(fileReq.getMediaType())){
            try {
                FrameGrabberKit.randomGrabberFFmpegImage(src).saveToDisk(this.fileProperty.getFileSavePath() + thumbnail);
                result.put("thumbnail", thumbnail);
            } catch (FrameGrabber.Exception e) {

            }
        }

        return Result.success(result);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam("path") String encodedPath){
        try {
            String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());
            java.io.File file = new java.io.File(fileProperty.getFileSavePath() + path);
            if (file.exists()) {
                try {
                    OutputStream outputStream = response.getOutputStream();
                    IOUtils.write(FileUtils.readFileToByteArray(file), outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/base64")
    public String base64(@RequestParam("path") String encodedPath){
        try {
            String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());
            String base64 = FileConvertUtil.toBase64(fileProperty.getFileSavePath() + path, "image/jpeg");
            return base64;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
