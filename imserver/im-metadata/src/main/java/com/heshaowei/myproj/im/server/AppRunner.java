package com.heshaowei.myproj.im.server;

import com.heshaowei.myproj.im.server.utils.VideoFrameUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    @Value("${tool-video.path}")
    private String toolVideoPath;
    @Value("${tool-video.name}")
    private String toolVideoName;

    @Override
    public void run(String... args) throws Exception {
        VideoFrameUtil.JAR_PATH = toolVideoPath;
        VideoFrameUtil.JAR_NAME = toolVideoName;
    }
}
