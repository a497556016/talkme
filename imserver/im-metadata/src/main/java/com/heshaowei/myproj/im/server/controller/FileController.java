package com.heshaowei.myproj.im.server.controller;

import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.server.dto.FileReq;
import com.heshaowei.myproj.im.server.enums.MediaTypes;
import com.heshaowei.myproj.im.server.model.File;
import com.heshaowei.myproj.im.server.repository.FileRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${file.save-path}")
    private String savePath;

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public Result upload(HttpServletRequest request, @RequestBody FileReq fileReq){
        File f = new File();

        String fileName = UUID.randomUUID().toString()+".jpg";
        String path = "\\im\\pictures\\"+fileName;
        f.setPath(path);
        f.setFileName(fileName);
        f.setContentType(MediaTypes.PICTURE.name());

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] buffer = decoder.decodeBuffer(fileReq.getFile());
            java.io.File dest = new java.io.File(this.savePath + path);
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdirs();
            }
            OutputStream outputStream = new FileOutputStream(dest);
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();

            this.fileRepository.save(f);

            return Result.success(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.error();
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam("path") String encodedPath){
        try {
            String path = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name());
            java.io.File file = new java.io.File(this.savePath + path);
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
}
