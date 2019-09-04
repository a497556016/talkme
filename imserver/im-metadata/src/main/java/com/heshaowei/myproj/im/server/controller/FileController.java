package com.heshaowei.myproj.im.server.controller;

import com.google.common.collect.Maps;
import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.server.dto.FileReq;
import com.heshaowei.myproj.im.server.enums.MediaTypes;
import com.heshaowei.myproj.im.server.model.File;
import com.heshaowei.myproj.im.server.properties.FileProperty;
import com.heshaowei.myproj.im.server.repository.FileRepository;
import com.heshaowei.myproj.im.server.utils.FileConvertUtil;
import com.heshaowei.myproj.utils.image.ImageHandler;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
        File f = new File();

        String fileName = UUID.randomUUID().toString();
        if(MediaTypes.AUDIO.equals(fileReq.getMediaType())){
            fileName += ".m4a";
            f.setContentType("audio/mpeg");
        }else if(MediaTypes.PICTURE.equals(fileReq.getMediaType())){
            fileName += "jpg";
            f.setContentType("image/jpeg");
        }
        String path = "\\im\\"+fileReq.getMediaType().name()+"\\"+fileName;
        f.setPath(path);
        f.setFileName(fileName);


        String src = fileProperty.getFileSavePath() + path;
        FileConvertUtil.fromBase64(fileReq.getFile(), src);
        this.fileRepository.save(f);

        Map<String, Object> result = Maps.newHashMap();
        result.put("path", path);
        //生成缩略图返回
        if(MediaTypes.PICTURE.equals(fileReq.getMediaType())) {
            try {
                byte[] buffer = new ImageHandler(src).scaleW(100).writeToBytes();
                String base64 = "data:image/jpeg;base64," + new BASE64Encoder().encode(buffer);
                result.put("thumbnail", base64);
            } catch (Exception e) {
                e.printStackTrace();
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
