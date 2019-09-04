package com.heshaowei.myproj.im.server.io;

import com.google.common.collect.Lists;
import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.metadata.client.controller.MessageClient;
import com.heshaowei.myproj.im.server.model.GroupMessage;
import com.heshaowei.myproj.im.server.model.Message;
import com.heshaowei.myproj.im.server.model.UserMessage;
import com.heshaowei.myproj.im.server.utils.GsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Log4j2
public class MessageRepositoryImpl implements MessageRepository, Runnable {

    @Autowired
    private MessageClient messageClient;

    private List<Message> messages = Lists.newArrayList();

    @Override
    public void push(Message message) {
        this.messages.add(message);
    }

    @Override
    public int save(Message message) {
        //只保存源路径，不保存base64数据
        message.setData(null);

        String json = GsonUtil.get().toJson(message);
        Result r = null;
        if(message instanceof UserMessage) {
            r = this.messageClient.saveUserMessage(json);
        }
        if(message instanceof GroupMessage) {
            r = this.messageClient.saveGroupMessage(json);
        }
        //保存失败，重新放进去
        if(null != r && r.getCode() == 0){
            log.error(GsonUtil.get().toJson(message) + "保存失败！");
            this.push(message);
        }
        return 0;
    }

    @Override
    public void run() {
        //轮询检查是否有新的消息，有就调用远程元数据服务保存
        while(true) {
            log.info("轮询检查需要保存的消息："+messages.size());

            if(messages.isEmpty()){
                //间隔时间
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            //取出第一个消息
            Message message = messages.remove(0);
            this.save(message);

            //间隔时间
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @PostConstruct
    private void init(){
        new Thread(this).start();
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> is = Lists.newArrayList();
        new Thread(() -> {
            while(true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(is.isEmpty()){
                    continue;
                }

                int i = is.remove(0);
                System.out.println(i);
            }
        }).start();



        for (int i = 0; i < 5; i++) {
            Thread.sleep(5000);
            is.add(i);
        }
    }
}
