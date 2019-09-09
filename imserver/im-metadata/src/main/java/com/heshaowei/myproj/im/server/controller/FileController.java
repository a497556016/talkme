package com.heshaowei.myproj.im.server.controller;

import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.server.dto.FileReq;
import com.heshaowei.myproj.im.server.enums.MediaTypes;
import com.heshaowei.myproj.im.server.model.FileInfo;
import com.heshaowei.myproj.im.server.properties.FileProperty;
import com.heshaowei.myproj.im.server.repository.FileRepository;
import com.heshaowei.myproj.im.server.utils.FileConvertUtil;
import com.heshaowei.myproj.im.server.utils.VideoFrameUtil;
import com.heshaowei.myproj.utils.image.GifUtils;
import com.heshaowei.myproj.utils.image.ImageHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        FileInfo f = new FileInfo();
        f.setMediaType(fileReq.getMediaType());

        String fileName = UUID.randomUUID().toString();
        String thumbnailName = fileName+"_thumb";
        String fileNameSuffix = "";
        String contentType = null;

        if(fileReq.getFile().startsWith("data:")&&fileReq.getFile().contains(";base64,")){
            contentType = fileReq.getFile().split(";base64,")[0].substring(5);
        }

        if(null != contentType){
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
        f.setContentType(contentType);
        String pathTemp = File.separatorChar+"im"+File.separatorChar+fileReq.getMediaType().name()+File.separatorChar+"%s"+fileNameSuffix;
        String path = String.format(pathTemp, fileName);
        f.setPath(path);
        f.setFileName(fileName);


        String src = fileProperty.getFileSavePath() + path;
        FileConvertUtil.fromBase64(fileReq.getFile(), src);

        //生成缩略图返回
        String thumbnail = String.format(pathTemp, thumbnailName);
        if(MediaTypes.PICTURE.equals(fileReq.getMediaType())) {
            if("image/gif".equals(contentType)){
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(this.fileProperty.getFileSavePath() + thumbnail));
                    GifUtils.zoomW(src, outputStream, 180);
                    f.setThumbnail(thumbnail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    new ImageHandler(src, this.fileProperty.getFileSavePath() + thumbnail).scaleW(100).writeToFile();
                    f.setThumbnail(thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if(MediaTypes.VIDEO.equals(fileReq.getMediaType())){
            try {
                VideoFrameUtil.getFirstFrame(src, this.fileProperty.getFileSavePath() + thumbnail);
                f.setThumbnail(thumbnail);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.fileRepository.save(f);

        return Result.success(f);
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
            String contentType = "image/jpeg";
            String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());

            if(StringUtils.isNotBlank(path)) {
                int begin = path.lastIndexOf(File.separatorChar) + 1;
                String fileName = path.substring(begin);

                int end = fileName.lastIndexOf(".");
                if(end > -1) {
                    fileName = fileName.substring(0, end);
                }

                if (fileName.contains("_")) {
                    fileName = fileName.split("_")[0];
                }

                FileInfo probe = new FileInfo();
                probe.setFileName(fileName);
                FileInfo fileInfo = this.fileRepository.findOne(Example.of(probe)).orElse(null);

                if (null != fileInfo) {
                    contentType = fileInfo.getContentType();
                }
            }

            String base64 = FileConvertUtil.toBase64(fileProperty.getFileSavePath() + path, contentType);
            return base64;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
