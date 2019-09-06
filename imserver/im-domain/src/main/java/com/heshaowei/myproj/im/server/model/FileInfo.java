package com.heshaowei.myproj.im.server.model;

import com.heshaowei.myproj.im.server.enums.MediaTypes;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "im_file")
@Data
public class FileInfo {
    @Id
    private ObjectId id;

    private String fileName;

    private String contentType;

    private String path;

    private String thumbnail;

    private MediaTypes mediaType;
}
