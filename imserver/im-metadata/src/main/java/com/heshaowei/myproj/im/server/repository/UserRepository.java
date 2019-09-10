package com.heshaowei.myproj.im.server.repository;

import com.heshaowei.myproj.im.server.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    List<User> findAllByUsernameIsNot(String username, Example<User> example);

    @Query(value = "{$and: [{'username': {$ne: ?0}}, {'$or': [{'username': {$regex: ?1}}, {'nickname': {$regex: ?1}}, {'phone': {$regex: ?1}}]}]}")
    List<User> queryByKeyWords(String username, String query, Pageable p);
}
