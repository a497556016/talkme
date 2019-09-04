package com.heshaowei.myproj.im.server.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class FileProperty {
    @Value("${file.save-path}")
    private String fileSavePath;
}
