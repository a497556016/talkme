package com.heshaowei.myproj.im.server.model;

import com.heshaowei.myproj.im.server.enums.MediaTypes;
import com.heshaowei.myproj.im.server.enums.MessageStates;
import com.heshaowei.myproj.im.server.enums.MessageTypes;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "im_message")
@Data
public class Message {
    @Id
    private ObjectId id;
    private MessageTypes type;
    private User from;
    /**
     * 消息内容
     */
    private String data;
    /**
     * 媒体文件原始路径
     */
    private String src;
    /**
     * 媒体类型
     */
    private MediaTypes mediaType;
    /**
     * 创建时间
     */
    private Date time;
    /**
     * 发送状态
     */
    private MessageStates sendState;
}
