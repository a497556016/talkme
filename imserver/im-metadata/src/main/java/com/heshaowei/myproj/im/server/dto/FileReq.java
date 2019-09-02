package com.heshaowei.myproj.im.server.dto;

import com.heshaowei.myproj.im.server.enums.MediaTypes;
import lombok.Data;

@Data
public class FileReq{
    private MediaTypes mediaType;
    private String file;

}