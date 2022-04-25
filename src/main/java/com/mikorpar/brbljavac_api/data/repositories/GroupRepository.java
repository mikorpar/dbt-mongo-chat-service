package com.mikorpar.brbljavac_api.data.repositories;

import com.mikorpar.brbljavac_api.data.dtos.groups.MessagesAndUsersDTO;
import com.mikorpar.brbljavac_api.data.dtos.groups.GroupWithoutMessagesDTO;
import com.mikorpar.brbljavac_api.data.dtos.users.UsersDTO;
import com.mikorpar.brbljavac_api.data.models.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {

    @Query(value="{ 'users' : ?0 }", fields="{ 'messages' : 0 }", sort = "{ 'updated_at': -1 }")
    List<GroupWithoutMessagesDTO> findByUsersContaining(ObjectId userId);

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $project: { _id: false, users: true, messages: true } }",
            "{ $unwind: { path: '$messages', preserveNullAndEmptyArrays: true } }",
            "{ $lookup: { from: 'users', localField: 'messages.user_id', foreignField: '_id', as: 'messages.user' } }",
            "{ $unwind: { path: '$messages.user', preserveNullAndEmptyArrays: true } }",
            "{ $group: { _id: null, users: { $addToSet: '$users' }, messages: { $push: '$messages' } } }",
            "{ $unwind: { path: '$users' } }",
            "{ $project: { _id: false, users: true, messages: { $cond: [ { $gt: [ { $first: '$messages._id' }, null ] }, '$messages', [] ] } } }",
            "{ $project: { 'messages.user.password': false, 'messages.user.conf_tokens': false } }"
    })
    MessagesAndUsersDTO findMsgsAndUsersByGroupId(String id);

    @Aggregation(pipeline = {
            "{ $match: { _id: ?0 } }",
            "{ $project: { _id: 0, users: 1 } }",
            "{ $lookup: { from: 'users', localField: 'users', foreignField: '_id', as: 'users' } }",
            "{ $project: { 'users.password': 0 } }"
    })
    Optional<UsersDTO> findGroupUsers(String id);
}
