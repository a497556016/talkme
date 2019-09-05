package com.heshaowei.myproj.im.server;

import com.heshaowei.myproj.im.server.io.MessageRepositoryImpl;
import com.heshaowei.myproj.im.server.io.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private MessageRepositoryImpl messageRepository;
    @Override
    public void run(String... args) throws Exception {
        this.nettyServer.init();
        this.messageRepository.init();
    }
}
