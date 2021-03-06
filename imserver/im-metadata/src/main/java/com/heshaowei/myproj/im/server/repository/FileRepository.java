package com.heshaowei.myproj.im.server.repository;

import com.heshaowei.myproj.im.server.model.FileInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<FileInfo, ObjectId> {
}
