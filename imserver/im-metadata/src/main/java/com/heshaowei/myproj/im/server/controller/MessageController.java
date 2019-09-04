package com.heshaowei.myproj.im.server.controller;

import com.google.common.collect.Lists;
import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.server.enums.MediaTypes;
import com.heshaowei.myproj.im.server.enums.MessageStates;
import com.heshaowei.myproj.im.server.model.GroupMessage;
import com.heshaowei.myproj.im.server.model.Message;
import com.heshaowei.myproj.im.server.model.User;
import com.heshaowei.myproj.im.server.model.UserMessage;
import com.heshaowei.myproj.im.server.properties.FileProperty;
import com.heshaowei.myproj.im.server.repository.GroupMessageRepository;
import com.heshaowei.myproj.im.server.repository.UserMessageRepository;
import com.heshaowei.myproj.im.server.utils.FileConvertUtil;
import com.heshaowei.myproj.im.server.utils.GsonUtil;
import com.heshaowei.myproj.im.server.utils.LoginUserUtil;
import com.heshaowei.myproj.utils.image.ImageHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private FileProperty fileProperty;


    @GetMapping("/queryHisUserMessages")
    public Result<Page<UserMessage>> queryHisUserMessages(String loginUsername, String lineUsername){
        if(null != loginUsername && null != lineUsername) {
            Sort sort = Sort.by(Sort.Direction.DESC,"time");
            Page<UserMessage> page = this.userMessageRepository.selectHisUserMessages(loginUsername, lineUsername, PageRequest.of(0, 10, sort));
            page.getContent().forEach(m -> toBase64DataFromSource(m));
            return Result.success(page);
        }
        return Result.error();
    }

    @GetMapping("/queryNotReceiveMessages")
    public Result<Page<UserMessage>> queryNotReceiveMessages(String loginUsername, String lineUsername){
        if(null != loginUsername && null != lineUsername) {
            Sort sort = Sort.by(Sort.Direction.ASC,"time");
            Page<UserMessage> page = this.userMessageRepository.selectNotReceiveMessages(loginUsername, lineUsername, PageRequest.of(0, 10, sort));
            //修改状态
            page.getContent().forEach(m -> m.setSendState(MessageStates.SUCCESS));
            this.userMessageRepository.saveAll(page.getContent());
            page.getContent().forEach(m -> toBase64DataFromSource(m));
            return Result.success(page);
        }
        return Result.error();
    }

    /**
     * 将源文件转换成base64格式
     * @param m
     */
    private void toBase64DataFromSource(Message m){
        if(null == m.getData() && null != m.getSrc()) {
            if(MediaTypes.AUDIO.equals(m.getMediaType())){
                String base64 = FileConvertUtil.toBase64(fileProperty.getFileSavePath() + m.getSrc(), "audio/mpeg");
                m.setData(base64);
            }else if(MediaTypes.PICTURE.equals(m.getMediaType())) {
                try {
                    byte[] buffer = new ImageHandler(fileProperty.getFileSavePath() + m.getSrc()).scaleW(100).writeToBytes();
                    String base64 = new BASE64Encoder().encode(buffer);
                    m.setData("data:image/jpeg;base64," + base64);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/saveUserMessage")
    public Result saveUserMessage(@RequestParam("message") String messageJson){
        UserMessage message = GsonUtil.get().fromJson(messageJson, UserMessage.class);
        if((MediaTypes.PICTURE.equals(message.getMediaType())||MediaTypes.AUDIO.equals(message.getMediaType()))
                &&!StringUtils.isBlank(message.getData())){
            //处理base64字符串在传输过程中丢失“+”号的问题
            message.setData(message.getData().replaceAll(" ", "+"));
        }
        this.userMessageRepository.save(message);
        return Result.success();
    }

    @PostMapping("/saveGroupMessage")
    public Result saveGroupMessage(@RequestParam("message") String messageJson){
        GroupMessage message = GsonUtil.get().fromJson(messageJson, GroupMessage.class);
        this.groupMessageRepository.save(message);
        return Result.success();
    }
}
